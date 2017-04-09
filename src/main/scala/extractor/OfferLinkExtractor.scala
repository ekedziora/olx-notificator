package extractor

import net.ruippeixotog.scalascraper.model.Document

trait OfferLinkExtractor {
  def extractOfferLinks(document: Document): List[String]
}
