package extractor

object OfferExtractorFactory {
  val OLX_URL = "www.olx.pl"
  val OTO_DOM_URL = "www.otodom.pl"

  def getOfferExtractor(url: String): OfferExtractor =
    if (url.contains(OfferExtractorFactory.OLX_URL))
      new OlxOfferExtractor
    else if (url.contains(OfferExtractorFactory.OTO_DOM_URL))
      new OtoDomOfferExtractor
    else
      throw new IllegalArgumentException(s"No offer extractor found for url $url")
}
