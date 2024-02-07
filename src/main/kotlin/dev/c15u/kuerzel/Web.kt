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

  fun extract(string: String, highlight: String): List<String> {
    if(highlight.isEmpty()) {
      return listOf(string)
    }

    var s = string
    val res = mutableListOf<String>()

    while (s.isNotEmpty()) {
      val i = s.indexOf(highlight, ignoreCase = true)
      when (i) {
        -1 -> {
          res.add(s)
          s = ""
        }

        0 -> {
          res.add(s.take(highlight.length))
          s = s.drop(highlight.length)
        }

        else -> {
          res.add(s.substring(0, i))
          s = s.drop(i)
        }
      }
    }

    return res
  }

  private inline fun HtmlBlockTag.resultsTable(
    results: Either<String, List<Abbreviation>>,
    highlight: String? = null
  ) {
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
                  highlightedTableDivision(highlight, a.abbreviation)
                  highlightedTableDivision(highlight, a.full)
                }
              }
            }
          )
        }
      }
    }
  }

  private fun TR.highlightedTableDivision(highlight: String?, cellContent:String) {
    td {
      if (highlight != null) {
        for (s in extract(cellContent, highlight)) {
          if (s.equals(highlight, ignoreCase = true)) {
            span(classes = "highlight") {
              +s
            }
          } else {
            +s
          }
        }
      } else {
        +cellContent
      }
    }
  }

  fun filter(query: String): String {
    return buildString {
      appendHTML().div {
        resultsTable(service.search(query), query)
      }
    }
  }
}
