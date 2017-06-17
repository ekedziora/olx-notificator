package model

import model.ProviderType.Provider
import play.twirl.api.Html

case class Offer(id: OfferId, url: String, title: String, subtitleHtml: Html, price: String,
                 imgUrl: String, descriptionHtml: Html)

object Offer {
  val EMPTY_STRING = ""
  val EMPTY_HTML = Html(EMPTY_STRING)

  def createOlxOffer(url: String, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(OfferId(id, ProviderType.Olx), url, title, subtitleHtml, price, imgUrl, descriptionHtml)

  def createOtoDomOffer(url: String, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(OfferId(id, ProviderType.OtoDom), url, title, subtitleHtml, price, imgUrl, descriptionHtml)

  def createGumtreeOffer(url: String, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(OfferId(id, ProviderType.Gumtree), url, title, subtitleHtml, price, imgUrl, descriptionHtml)

  def createGratkaOffer(url: String, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(OfferId(id, ProviderType.Gratka), url, title, subtitleHtml, price, imgUrl, descriptionHtml)
}
