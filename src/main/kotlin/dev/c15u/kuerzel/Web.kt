package dev.c15u.kuerzel

import arrow.core.getOrElse
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML

class Web(val service: Service) {
  fun index(): String {
    return createHTML(true).html {

      headers()

      body {

        script(src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js") { }

        div(classes = "container") {
          div {
            id = "toolbar"

            div(classes = "container") {
              id = "search"
              input(type = InputType.text, name = "q") {
                placeholder = "Search here"
                attributes["hx-get"] = "/web/filter"
                attributes["hx-trigger"] = "keyup changed"
                attributes["hx-target"] = "#search-results"
                attributes["autofocus"] = ""
              }
            }

            div(classes = "container") {
              id = "add"
              a(href = "/web/add.html") {
                +"Add"
              }
            }
          }

          resultsTable(service.all().map { it.map { it to 1.0 } }.getOrElse {
            throw RuntimeException(it)
          })
        }
      }
    }.toString()
  }

  private fun HTML.headers() {
    head {
      meta { charset = "utf-8" }
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
  }

  fun extract(string: String, highlight: String): List<String> {
    if (highlight.isEmpty()) {
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

  private fun HtmlBlockTag.resultsTable(
    results: List<Pair<AbbreviationHistory, Double>>,
    highlight: String? = null
  ) {
    div(classes = "container") {
      div(classes = "row justify-content-center") {
        id = "search-results"
        table(classes = "table") {
          tr {
            th { +"" }
            th { +"Abbreviation" }
            th { +"Full" }
          }

          results
            .filter { it.second == 0.0 }
            .forEach { a ->
              tr {
                td { a(href = "/web/edit.html?id=" + a.first.id) { +"Edit" } }
                highlightedTableDivision(highlight, a.first.mostRecent().abbreviation.short)
                highlightedTableDivision(highlight, a.first.mostRecent().abbreviation.full)
              }
            }
          tr {
            id = "split"
            td {
              colSpan = "3"
              +"―― Fuzzy ――"
            }
          }
          results
            .filter { it.second > 0 }
            .sortedBy { it.second }
            .forEach { a ->
              tr {
                style = "opacity: ${100.0 * (1.0 - (0.3 * a.second / Config.MAX_DIFFERENCE))}%"
                td { a(href = "/web/edit.html?id=" + a.first.id) { +"Edit" } }
                highlightedTableDivision(highlight, a.first.mostRecent().abbreviation.short)
                highlightedTableDivision(highlight, a.first.mostRecent().abbreviation.full)
              }
            }
        }
      }
    }
  }

  private fun TR.highlightedTableDivision(highlight: String?, cellContent: String) {
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
        resultsTable(service.search(query).getOrElse { listOf() }, query)
      }
    }
  }

  fun add(): String {
    return createHTML(true).html {
      headers()
      body {
        container {
          form {
            attributes["hx-post"] = "/web/add.html"
            textInput("Abbreviation", "short")
            br {}
            textInput("Full", "full")
            br {}
            br {}
            input(type = InputType.submit) {
              attributes["onclick"] = "window.location.href = '/index.html';"
            }
          }
        }
      }
    }
  }

  private fun FORM.textInput(label: String, forWhat: String, value: String? = null) {
    div(classes = "form-group") {
      label {
        attributes["for"] = forWhat
        +label
      }
      br { }
      input(type = InputType.text, classes = "form-control") {
        attributes["id"] = forWhat
        attributes["name"] = forWhat
        value?.let { attributes["value"] = value }
      }
    }
  }

  private fun BODY.container(block: DIV.() -> Unit = {}) {
    div(classes = "container") {
      block(this)
    }
  }

  fun edit(id: String): String {
    val element = service.byId(id)

    return createHTML(true).html {
      headers()
      body {
        container {
          element.fold(
            {
              p {
                +"Nothing to edit"
              }
              input(type = InputType.button) {
                attributes["onclick"] = "window.location.href = '/index.html';"
              }
            },
            { h ->
              div(classes = "container") {

                h2 { +"Update" }

                div(classes = "container") {
                  form {
                    attributes["hx-post"] = "/web/edit"
                    textInput("Abbreviation", "short", h.mostRecent().abbreviation.short)
                    br {}
                    textInput("Full", "full", h.mostRecent().abbreviation.full)
                    br {}
                    br {}
                    input(type = InputType.hidden) {
                      attributes["id"] = id
                      attributes["name"] = "id"
                      attributes["value"] = id
                    }
                    input(type = InputType.submit) {
                      attributes["onclick"] = "window.location.href = '/index.html';"
                    }
                  }
                }

                h3 { +"History" }

                div(classes = "container") {
                  div(classes = "row justify-content-center") {
                    this.id = "edit-history"
                    table(classes = "table") {
                      tr {
                        th { +"Date" }
                        th { +"Abbreviation" }
                        th { +"Full" }
                      }

                      h.revisions.sortedByDescending { it.date }.forEach { r ->
                        tr {
                          td { +r.date.replace("T", " ").takeWhile { it != '.' } }
                          td { +r.abbreviation.short }
                          td { +r.abbreviation.full }
                        }
                      }
                    }
                  }
                }
              }
            }
          )
        }
      }
    }
  }
}
