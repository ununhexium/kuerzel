package dev.c15u.kuerzel

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Query
import org.http4k.lens.string
import org.http4k.routing.bind
import org.http4k.routing.headers
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer

val routed = { service: Service ->
  routes(
    "/api/add" bind POST to {
      service.add(Abbreviation.lens(it))
        .fold(
          { Response(BAD_REQUEST) },
          { Response(OK).with(Abbreviation.lens of it) }
        )
    },
    "/api/all" bind GET to {
      service.all()
        .fold(
          { Response(BAD_REQUEST).body(it) },
          { Response(OK).with(Body.auto<List<Abbreviation>>().toLens() of it) }
        )
    },
    "/style.css" bind GET to {
      Response(OK).body(
        Abbreviation::class.java.getResourceAsStream("/style.css")?.reader()?.readText() ?: ""
      )
    },
    "/web/filter" bind GET to {
      Response(OK).body(
        Query.string().required("q")(it)
      )
    },
    "/index.html" bind GET to {
      Response(OK).body(
        createHTML(true).html {

          head {
            link(href = "/style.css", rel = "stylesheet")
            script(src = "https://cdnjs.cloudflare.com/ajax/libs/htmx/1.9.10/htmx.min.js") {
            }
          }

          body {

            div {
              id = "search"
              input(type = InputType.text, name = "q") {
                placeholder = "Search here"
                attributes["hx-get"] = "/web/filter"
                attributes["hx-trigger"] = "keyup delay:200ms changed"
                attributes["hx-target"] = "#search-results"
              }
            }

            div {
              id = "search-results"
              table {
                headers("abbreviation", "full")
                service.all().fold(
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
        }.toString()
      )
    }
  )
}

fun main() {
  routed(Service()).asServer(Undertow(9000)).start()
}