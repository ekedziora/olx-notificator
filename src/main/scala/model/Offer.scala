package model

import model.ProviderType.Provider
import play.twirl.api.Html

case class Offer(provider: Provider, url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String,
                 imgUrl: String, id: String, descriptionHtml: Html)

object Offer {
  val EMPTY_STRING = ""
  val EMPTY_HTML = Html(EMPTY_STRING)
  val NULL_OBJECT = Offer(null, EMPTY_STRING, EMPTY_HTML, EMPTY_STRING, EMPTY_HTML, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_HTML)

  def createOlxOffer(url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(ProviderType.Olx, url, pureHtml, title, subtitleHtml, price, imgUrl, id, descriptionHtml)

  def createOtoDomOffer(url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(ProviderType.OtoDom, url, pureHtml, title, subtitleHtml, price, imgUrl, id, descriptionHtml)
}
