package extractor
import model.Offer
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.text
import play.twirl.api.Html
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

class OlxOfferExtractor extends OfferExtractor {

  private final val idPattern = "\\d+$".r

  override def extractOffer(offerElement: Element, link: String): Offer = {
    val imgUrl = offerElement tryExtract element("div .photo-handler") flatMap(_ tryExtract element("img")) map(_.attr("src"))
    val price = offerElement tryExtract element(".price-label") map (_ extract text("strong"))
    val titleBox = offerElement tryExtract element(".offer-titlebox")
    val title = titleBox flatMap(_ tryExtract text("h1"))
    val subtitle = titleBox flatMap (_ tryExtract element(".offer-titlebox__details"))
    val offerIdString = subtitle flatMap (_ tryExtract text("small"))
    val offerId = idPattern.findFirstIn(orEmptyString(offerIdString))
    val subtitleHtml = subtitle.map(_.innerHtml)
    val descriptionHtml = (offerElement tryExtract element("div .clr .descriptioncontent")).map(_.outerHtml)
    Offer.createOlxOffer(link, Html(offerElement.innerHtml), orEmptyString(title), Html(orEmptyString(subtitleHtml)), orEmptyString(price),
      orEmptyString(imgUrl), orEmptyString(offerId), Html(orEmptyString(descriptionHtml)))
  }
}
