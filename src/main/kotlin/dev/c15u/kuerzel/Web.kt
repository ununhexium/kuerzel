package dev.c15u.kuerzel

import arrow.core.Either
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML

class Web(val service: Service) {
  fun index(): String {
    return createHTML(true).html {

      head {
        link(
          rel = "stylesheet",
          href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
        )
        link(
          rel = "stylesheet",
          href = "https://cdn.jsdelivr.net/npm/bootstrap@3.4.1/dist/css/bootstrap-theme.min.css"
        )
        link(rel = "stylesheet", href = "/style.css")

        script(src = "https://cdnjs.cloudflare.com/ajax/libs/htmx/1.9.10/htmx.min.js") {
        }
      }

      body {

        script(src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js") { }

        div {
          div {
            id = "search"
            input(type = InputType.text, name = "q") {
              placeholder = "Search here"
              attributes["hx-get"] = "/web/filter"
              attributes["hx-trigger"] = "keyup delay:100ms changed"
              attributes["hx-target"] = "#search-results"
              attributes["autofocus"] = ""
            }
          }

          resultsTable(service.all())
        }

      }
    }.toString()
  }

  inline fun HtmlBlockTag.resultsTable(results: Either<String, List<Abbreviation>>) {
    div(classes = "container") {
      div(classes = "row justify-content-center") {
        id = "search-results"
        table(classes = "table") {
          tr {
            th { +"Abbreviation" }
            th { +"Full" }
          }
          results.fold(
            {},
            {
              it.forEach { a ->
                tr {
                  td { +a.abbreviation }
                  td { +a.full }
                }
              }
            }
          )
        }
      }
    }
  }

  fun filter(query: String): String {
    return buildString {
      appendHTML().div {
        resultsTable(service.search(query))
      }
    }
  }
}
