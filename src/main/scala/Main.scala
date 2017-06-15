import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.mail.internet.MimeBodyPart

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import com.typesafe.scalalogging.LazyLogging
import _root_.extractor.OfferExtractorFactory
import courier.Envelope
import courier.Multipart
import model.Offer
import model.OfferId
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import play.twirl.api.Html

import scala.concurrent.ExecutionException
import scala.util.Try
import courier._
import Defaults._
import _root_.extractor.OfferExtractor
import _root_.extractor.OfferExtractor.stringToInt

import scala.util.Failure
import scala.util.Success

object Main extends LazyLogging {

  private val DEFAULT_INTERVAL = 60

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println("Too few arguments. 1-mails receivers separated by comma, 2-offers url separated by '|', 3-check interval in seconds (optional)")
    }

    val executor = new ScheduledThreadPoolExecutor(1)
    val errorHandler = new ScheduledThreadPoolExecutor(1)

    val sender = sys.env.getOrElse("sender", throw new IllegalStateException("Please set sender environment variable"))
    val senderPassword = sys.env.getOrElse("senderPassword", throw new IllegalStateException("Please set sender password environment variable"))
    val mailer: Mailer = Mailer("smtp.gmail.com", 587)
      .auth(true)
      .as(sender, senderPassword)
      .startTtls(true)()

    val receivers = args(0).split(',').map(_.trim).filter(_.nonEmpty)
    val offersPages = args(1).split('|').map(_.trim).filter(_.nonEmpty)
    val interval = args.lift(2).flatMap(stringToInt).getOrElse(DEFAULT_INTERVAL)
    offersPages
      .map(url => new Job(mailer, url, sender, List(receivers: _*)))
      .zipWithIndex
      .map { case (job, idx) => executor.scheduleAtFixedRate(job, idx * (interval/offersPages.length), interval, TimeUnit.SECONDS) }
      .foreach { future =>
        errorHandler.execute(() =>
          try {
            future.get()
          } catch {
            case e: InterruptedException => logger.error("Scheduled execution was interrupted", e)
            case e: CancellationException => logger.error("Watcher thread has been cancelled", e)
            case e: ExecutionException =>
              logger.error(s"Uncaught exception in scheduled execution", e.getCause)
              future.cancel(true)
          })
      }
  }
}

class Job(mailer: Mailer, url: String, sender: String, receivers: List[String]) extends Runnable with LazyLogging {

  var lastServedOfferId: Option[OfferId] = None

  override def run(): Unit = try {
    val browser = JsoupBrowser()
    val allOffersList = for {
      link <- OfferExtractorFactory.getOfferLinkExtractor(url).extractOfferLinks(browser.get(url))
      offerHtml <- browser.get(link)
    } yield OfferExtractorFactory.getOfferExtractor(link).extractOffer(offerHtml, link)

    val offersList = lastServedOfferId.map(id => allOffersList.takeWhile(offer => offer.id != id))
      .getOrElse(allOffersList)
    offersList.headOption
      .foreach(head => lastServedOfferId = Some(head.id))

    if (offersList.nonEmpty) {
      val time = ZonedDateTime.now(ZoneId.of("Europe/Warsaw")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
      val part = new MimeBodyPart
      part.setText(template.html.mailTemplate(offersList).toString(), StandardCharsets.UTF_8.name(), "html")
      mailer(Envelope.from(sender.addr)
        .to(receivers.map(_.addr) : _*)
        .subject("Aktualizacja ogłoszeń: " + time)
        .content(Multipart().add(part)))
        .onComplete {
          case Success(v) => logger.info("Report sended on " + time)
          case Failure(ex) => throw ex
        }
    }
  } catch {
    case t: Throwable =>
      logger.error("Exception in task", t)
  }
}
