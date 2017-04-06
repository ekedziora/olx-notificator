package extractor
import model.Offer
import net.ruippeixotog.scalascraper.model.Element
import play.twirl.api.Html

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

class OtoDomOfferExtractor extends OfferExtractor {

  private final val idPattern = "^https:\\/\\/www\\.otodom\\.pl\\/oferta\\/\\S+ID(\\S+)\\.html#".r

  private final val idPat = "\\d+".r

  override def extractOffer(offerElement: Element, link: String): Offer = {
    val titleElement = offerElement tryExtract element(".section-offer-title")
    val title = titleElement flatMap (_ tryExtract text("h1"))
    val subtitle = titleElement flatMap (_ tryExtract element("address")) map(_.innerHtml)
    val price = titleElement flatMap (_ tryExtract text("strong.box-price-value"))
    val imageUrl = offerElement tryExtract elements(".gallery-box-thumbs") flatMap (_ tryExtract element("a:first-of-type")) map(_ attr "href")
    val description = offerElement tryExtract element(".text-contents") map (_.innerHtml)
    val offerParameters = offerElement tryExtract element(".section-offer-params") flatMap (_ tryExtract element(".col-md-offer-content")) map (_.innerHtml)
    val offerId = offerElement tryExtract element(".text-details") flatMap (_ tryExtract text(".left")) flatMap (text => idPat.findFirstIn(text))
    Offer.createOtoDomOffer(link, Html(offerElement.innerHtml), orEmptyString(title), Html(orEmptyString(subtitle)), orEmptyString(price), orEmptyString(imageUrl),
      orEmptyString(offerId), Html(orEmptyString(offerParameters) + orEmptyString(description)))
  }
}
