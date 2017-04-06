import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
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

import scala.util.Failure
import scala.util.Success

object Main extends LazyLogging {

  val mailer: Mailer = Mailer("smtp.gmail.com", 587).auth(true)
    .as("ekedziora13@gmail.com", "dcejjhnszrvtpilw")
    .startTtls(true)()

  def main(args: Array[String]): Unit = {
    val executor = new ScheduledThreadPoolExecutor(1)
    val errorHandler = new ScheduledThreadPoolExecutor(1)

    val runnable: Runnable = new Runnable {
      val exceptionsMap = new ConcurrentHashMap[Class[_ <: Throwable], Int]()
      override def run(): Unit = try {

      } catch {
        case t: Throwable =>
          logger.error("Exception in task", t)
          exceptionsMap.put(t.getClass, exceptionsMap.getOrDefault(t.getClass, 1) + 1)
          if (exceptionsMap.get(t.getClass) > 5) {
            throw new IllegalStateException(s"Exception of type ${t.getClass} was thrown more then 5 times. Ending!")
          }
      }
    }

    val future = executor.scheduleAtFixedRate(new Job(mailer), 0, 20, TimeUnit.SECONDS)
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

class Job(mailer: Mailer) extends Runnable with LazyLogging {

  var lastServedOfferId: Option[OfferId] = None

  val exceptionsMap = new ConcurrentHashMap[Class[_ <: Throwable], Int]()

  override def run(): Unit = try {
    val browser = JsoupBrowser()
    val doc = browser.get("https://www.olx.pl/nieruchomosci/mieszkania/sprzedaz/warszawa/?search%5Bfilter_enum_market%5D%5B0%5D=primary")
    val allOffersList = for {
      link <- doc extract element("#offers_table tbody") extract elementList(".offer") map (_ extract element("a")) map(_ attr "href")
      offerHtml <- browser.get(link)
    } yield OfferExtractorFactory.getOfferExtractor(link).extractOffer(offerHtml, link)

    val offersList = lastServedOfferId.map(id => allOffersList.takeWhile(offer => offer.id != id)).getOrElse(allOffersList)
    offersList.headOption.foreach(head => lastServedOfferId = Some(head.id))

    new PrintWriter("result.html") {
      write(template.html.mailTemplate(offersList).toString())
      close()
    }
    println("SAVED")

    if (offersList.nonEmpty) {
      val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
      val part = new MimeBodyPart
      part.setText(template.html.mailTemplate(offersList).toString(), StandardCharsets.UTF_8.name(), "html")
      mailer(Envelope.from("ekedziora13@gmail.com".addr)
        .to("ekedziora@hotmail.com".addr)
        .subject("Aktualizacja ogłoszeń: " + time)
        .content(Multipart().add(part)))
        .onComplete {
          case Success(v) => println("delivered report")
          case Failure(ex) => throw ex
        }
    }
  } catch {
    case t: Throwable =>
      logger.error("Exception in task", t)
      exceptionsMap.put(t.getClass, exceptionsMap.getOrDefault(t.getClass, 1) + 1)
      if (exceptionsMap.get(t.getClass) > 5) {
        throw new IllegalStateException(s"Exception of type ${t.getClass} was thrown more then 5 times. Ending!")
      }
  }

  private def stringToInt(string: String): Option[Int] = Try(Integer.parseInt(string)) map(Option(_)) getOrElse None
}
