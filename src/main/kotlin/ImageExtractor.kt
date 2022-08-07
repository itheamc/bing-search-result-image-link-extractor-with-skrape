import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.body
import it.skrape.selects.html5.div
import it.skrape.selects.html5.li
import it.skrape.selects.html5.ul
import org.json.JSONException
import org.json.JSONObject

class ImageExtractor {
    companion object {
        suspend fun findImageLinks(query: String): ArrayList<String> {
            val lists = skrape(AsyncFetcher) {
                request {
                    url = "https://www.bing.com/images/search?q=${
                        query.replace(
                            " ",
                            "+"
                        )
                    }&qft=+filterui:photo-clipart+filterui:licenseType-Any"
                }
                response {
                    htmlDocument {
                        body {
                            div("#b_content") {
                                div("#vm_c") {
                                    div(".dg_b") {
                                        div("#mmComponent_images_2") {
                                            ul {
                                                li {
                                                    findAll {
                                                        this
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Creating new list to store image url link
            val imageUrls = arrayListOf<String>()

            // Extracting image url from the list
            for (e in lists) {
                if (e.hasAttribute("data-idx")) {
                    for (e1 in e.children) {
                        for (e2 in e1.children) {
                            for (e3 in e2.children) {
                                if (e3.tagName == "a") {
                                    try {
                                        val s = e3.attribute("m")
                                        val o = JSONObject(s)
                                        val l = o.getString("murl")
                                        if (l.contains(".jpg") || l.contains(".png")) {
                                            imageUrls.add(l)
                                        }
                                    } catch (e: JSONException) {
                                        continue
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return imageUrls
        }
    }
}
