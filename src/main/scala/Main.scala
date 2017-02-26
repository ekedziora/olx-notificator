import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import com.typesafe.scalalogging.LazyLogging
import _root_.extractor.OfferExtractorFactory
import model.Offer
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import play.twirl.api.Html

import scala.concurrent.ExecutionException
import scala.util.Try

object Main extends LazyLogging {
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

    val future = executor.scheduleAtFixedRate(new Job, 0, 1, TimeUnit.SECONDS)
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

class Job extends Runnable with LazyLogging {
  val exceptionsMap = new ConcurrentHashMap[Class[_ <: Throwable], Int]()

  override def run(): Unit = try {
    val browser = JsoupBrowser()
    val doc = browser.get("https://www.olx.pl/nieruchomosci/mieszkania/sprzedaz/warszawa/?search%5Bfilter_enum_market%5D%5B0%5D=primary")
    val offersList = for {
      link <- doc extract element("#offers_table tbody") extract elementList(".offer") map (_ extract element("a")) map(_ attr "href")
      offerHtml <- browser.get(link)
    } yield OfferExtractorFactory.getOfferExtractor(link).extractOffer(offerHtml, link)
    logger.debug(template.html.mailTemplate(offersList).toString())
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
