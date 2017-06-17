package extractor
import model.Offer
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import play.twirl.api.Html

class GratkaOfferExtractor extends OfferExtractor {

  private final val idPattern = "\\d{3}-\\d+".r

  def extractOffer(offerElement: Element, link: String): Offer = {
    val offerId = idPattern.findFirstIn(link)
    val titleDiv = offerElement tryExtract element("div.clearOver.clear")
    val title = titleDiv flatMap (_ tryExtract text("h1"))
    val subtitle = titleDiv flatMap (_ tryExtract element("h2")) map (_.innerHtml)
    val price = offerElement tryExtract element("div.cenaGlowna") flatMap (_ tryExtract text("p"))
    val imgUrl = offerElement tryExtract element("div.slides.links") flatMap (_ tryExtract element("img")) map (_ attr "src")
    val descriptionElement = offerElement tryExtract element("div#dane-podstawowe")
    val description = descriptionElement map (_.outerHtml)
    Offer.createGratkaOffer(link, orEmptyString(title), Html(orEmptyString(subtitle)),
      orEmptyString(price), orEmptyString(imgUrl), orEmptyString(offerId), Html(orEmptyString(description)))
  }
}
