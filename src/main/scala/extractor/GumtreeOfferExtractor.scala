package extractor
import model.Offer
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.text
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import play.twirl.api.Html

class GumtreeOfferExtractor extends OfferExtractor {

  private final val idPattern = "\\d+$".r

  override def extractOffer(offerElement: Element, link: String): Offer = {
    val imgUrl = offerElement tryExtract element("div .vip-gallery") flatMap(_ tryExtract element("img")) map(_.attr("src"))
    val price = offerElement tryExtract element("div .price") flatMap (_ tryExtract text(".amount"))
    val title = offerElement tryExtract element(".item-title") flatMap (_ tryExtract text(".myAdTitle"))
    val offerIdString = offerElement tryExtract text(".title")
    val offerId = idPattern.findFirstIn(orEmptyString(offerIdString))
    val descriptionHtml = offerElement tryExtract element("div .vip-details").map(_.outerHtml)
    Offer.createGumtreeOffer(link, orEmptyString(title), Html(""), orEmptyString(price),
      orEmptyString(imgUrl), orEmptyString(offerId), Html(orEmptyString(descriptionHtml)))
  }
}
