package dev.c15u.kuerzel

import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.meta
import kotlinx.html.stream.createHTML
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Query
import org.http4k.lens.string
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer

val routed = { service: Service ->
  val web = Web(service)

  routes(
    "/api/add" bind POST to {
      service.add(Abbreviation.lens(it))
        .fold({ Response(BAD_REQUEST) }, { Response(OK).with(Abbreviation.lens of it) })
    },
    "/api/all" bind GET to {
      service.all().fold({ Response(BAD_REQUEST).body(it) },
        { Response(OK).with(Body.auto<List<Abbreviation>>().toLens() of it.map { it.first }) })
    },
    "/style.css" bind GET to {
      Response(OK).body(
        Abbreviation::class.java.getResourceAsStream("/style.css")?.reader()?.readText() ?: ""
      )
    },
    "/index.html" bind GET to {
      Response(OK).body(web.index())
    },
    "/index.html" bind POST to {
      val abbreviation = it.form("abbreviation") ?: ""
      val full = it.form("full") ?: ""
      service.add(Abbreviation(abbreviation, full))
        .fold({ Response(BAD_REQUEST) }, { Response(OK).with(Abbreviation.lens of it) })
      Response(OK).body(web.index())
    },
    "/web/add" bind GET to {
      Response(OK).body(
        web.add()
      )
    },
    "/web/filter" bind GET to {
      Response(OK).body(
        web.filter(Query.string().required("q")(it))
      )
    }
  )
}

fun main() {
  routed(Service()).asServer(Undertow(9000)).start()
}