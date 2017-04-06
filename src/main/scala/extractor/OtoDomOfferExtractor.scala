package extractor
import model.Offer
import net.ruippeixotog.scalascraper.model.Element
import play.twirl.api.Html

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

class OtoDomOfferExtractor extends OfferExtractor {

  private final val idPattern = "^https:\\/\\/www\\.otodom\\.pl\\/oferta\\/\\S+ID(\\S+)\\.html#".r

  override def extractOffer(offerElement: Element, link: String): Offer = {
    val titleElement = offerElement tryExtract element(".section-offer-title")
    val title = titleElement flatMap (_ tryExtract text("h1"))
    val subtitle = titleElement flatMap (_ tryExtract element("address")) map(_.innerHtml)
    val price = titleElement flatMap (_ tryExtract text("strong.box-price-value"))
    val imageUrl = (offerElement tryExtract elements(".gallery-box-image-item")) map(_ extract element("img") attr "src")
    val offerContent = offerElement tryExtract element(".col-md-offer-content") map (_.innerHtml)
    val description = offerElement tryExtract element(".text-contents") map (_.innerHtml)
    val offerId = idPattern.findFirstMatchIn(link)
      .filter(m => m.groupCount > 0)
      .map(m => m.group(1))
    Offer.createOtoDomOffer(link, Html(offerElement.innerHtml), orEmptyString(title), Html(orEmptyString(subtitle)), orEmptyString(price), orEmptyString(imageUrl),
      orEmptyString(offerId), Html(orEmptyString(offerContent) + orEmptyString(description)))
  }
}
