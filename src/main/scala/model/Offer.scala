package model

import play.twirl.api.Html

case class Offer(url: String, pureHtml: Html, title: String, subtitleHtml: Html, price: String, imgUrl: String, id: Long, descriptionHtml: Html) {

}

object Offer {
  val EMPTY_STRING = ""
  val EMPTY_HTML = Html(EMPTY_STRING)
  val NULL_OBJECT = Offer(EMPTY_STRING, EMPTY_HTML, EMPTY_STRING, EMPTY_HTML, EMPTY_STRING, EMPTY_STRING, 0, EMPTY_HTML)
}
