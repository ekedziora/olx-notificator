package extractor
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.dsl.DSL._
import _root_.extractor.OfferExtractorFactory.GRATKA_URL
import _root_.extractor.OfferExtractorFactory.HTTP_PREFIX
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

class GratkaOfferLinkExtractor extends OfferLinkExtractor {
  override def extractOfferLinks(document: Document): List[String] =
    document extract element("#list-ads") extract elementList("li.standard:not(.wyroznione)") map (_ extract element("a") attr "href") map (link => s"$HTTP_PREFIX$GRATKA_URL$link")
}
