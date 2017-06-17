package model

import model.ProviderType.Provider
import play.twirl.api.Html

case class Offer(id: OfferId, url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String,
                 imgUrl: String, descriptionHtml: Html)

object Offer {
  val EMPTY_STRING = ""
  val EMPTY_HTML = Html(EMPTY_STRING)

  def createOlxOffer(url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(OfferId(id, ProviderType.Olx), url, pureHtml, title, subtitleHtml, price, imgUrl, descriptionHtml)

  def createOtoDomOffer(url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(OfferId(id, ProviderType.OtoDom), url, pureHtml, title, subtitleHtml, price, imgUrl, descriptionHtml)

  def createGumtreeOffer(url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(OfferId(id, ProviderType.Gumtree), url, pureHtml, title, subtitleHtml, price, imgUrl, descriptionHtml)

  def createGratkaOffer(url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: String, descriptionHtml: Html) =
    new Offer(OfferId(id, ProviderType.Gratka), url, pureHtml, title, subtitleHtml, price, imgUrl, descriptionHtml)
}
