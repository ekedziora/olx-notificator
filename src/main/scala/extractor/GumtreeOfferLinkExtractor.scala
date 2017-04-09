package extractor
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.dsl.DSL._
import _root_.extractor.OfferExtractorFactory.HTTPS_PREFIX
import _root_.extractor.OfferExtractorFactory.GUMTREE_URL
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

class GumtreeOfferLinkExtractor extends OfferLinkExtractor {
  override def extractOfferLinks(document: Document): List[String] =
    document extract element("[class=\"view\"]") extract elementList(".href-link") map(_ attr "href") map (link => s"$HTTPS_PREFIX$GUMTREE_URL$link")
}
