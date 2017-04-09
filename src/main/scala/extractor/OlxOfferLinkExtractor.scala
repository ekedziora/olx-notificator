package extractor
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

class OlxOfferLinkExtractor extends OfferLinkExtractor {
  override def extractOfferLinks(document: Document): List[String] =
    document extract element("#offers_table tbody") extract elementList(".offer") map (_ extract element("a")) map(_ attr "href")
}
