package extractor
import model.Offer
import net.ruippeixotog.scalascraper.model.Element

class OtoDomOfferExtractor extends OfferExtractor {
  override def extractOffer(offerElement: Element, link: String): Offer = Offer.NULL_OBJECT
}
