package extractor

import model.Offer
import model.Offer.EMPTY_STRING
import net.ruippeixotog.scalascraper.model.Element

import scala.util.Try

trait OfferExtractor {

  def extractOffer(offerElement: Element, link: String): Offer

  protected def orEmptyString(optionalString: Option[String]): String = optionalString.getOrElse(EMPTY_STRING)

}

object OfferExtractor {
  def stringToInt(string: String): Option[Int] = Try(Integer.parseInt(string)) map(Option(_)) getOrElse None
}
