package extractor

object OfferExtractorFactory {
  val HTTPS_PREFIX = "https://"
  val HTTP_PREFIX = "http://"
  val OLX_URL = "www.olx.pl"
  val OTO_DOM_URL = "www.otodom.pl"
  val GUMTREE_URL = "www.gumtree.pl"
  val GRATKA_URL = "dom.gratka.pl"

  def getOfferExtractor(url: String): OfferExtractor =
    if (url.contains(OLX_URL))
      new OlxOfferExtractor
    else if (url.contains(OTO_DOM_URL))
      new OtoDomOfferExtractor
    else if (url.contains(GUMTREE_URL))
      new GumtreeOfferExtractor
    else if (url.contains(GRATKA_URL))
      new GratkaOfferExtractor
    else
      throw new IllegalArgumentException(s"No offer extractor found for url $url")

  def getOfferLinkExtractor(url: String): OfferLinkExtractor =
    if (url.contains(OLX_URL))
      new OlxOfferLinkExtractor
    else if (url.contains(GUMTREE_URL))
      new GumtreeOfferLinkExtractor
    else if (url.contains(GRATKA_URL))
      new GratkaOfferLinkExtractor
    else
      throw new IllegalArgumentException(s"No offer link extractor found for url $url")
}
