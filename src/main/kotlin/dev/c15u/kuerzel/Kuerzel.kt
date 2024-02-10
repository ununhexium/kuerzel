package dev.c15u.kuerzel

import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
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
import java.nio.file.Paths

val routed = { service: Service ->
  val web = Web(service)

  routes(
    "/api/add" bind POST to {
      val a = Abbreviation.lens(it)
      service.add(a.short, a.full)
        .fold(
          { Response(BAD_REQUEST) },
          { Response(OK).with(AbbreviationHistory.lens of it) }
        )
    },
    "/api/all" bind GET to {
      service.all().fold({ Response(BAD_REQUEST).body(it) },
        {
          Response(OK).with(
            Body.auto<List<AbbreviationHistory>>().toLens() of it.map { it })
        })
    },
    "/style.css" bind GET to {
      Response(OK).body(
        AbbreviationHistory::class.java.getResourceAsStream("/style.css")?.reader()?.readText()
          ?: ""
      )
    },
    "/index.html" bind GET to {
      Response(OK).body(web.index())
    },
    "/web/add.html" bind POST to {
      val abbreviation = it.form("abbreviation") ?: ""
      val full = it.form("full") ?: ""
      service.add(abbreviation, full)
        .fold({ Response(BAD_REQUEST) }, { Response(OK).with(AbbreviationHistory.lens of it) })
      Response(OK).body(web.index())
    },
    "/web/add.html" bind GET to {
      Response(OK).body(
        web.add()
      )
    },
    "/web/edit.html" bind GET to {
      Response(OK).body(
        web.edit(Query.required("id")(it))
      )
    },
    "/web/edit.html" bind POST to {
      service.update(
        Query.required("id")(it),
        Query.required("abbreviation")(it),
        Query.required("full")(it),
      ).fold(
        {
          Response(OK).body(
            ""
          )
        },
        {
          Response(OK).body(
            ""
          )
        }
      )
    },
    "/web/filter" bind GET to {
      Response(OK).body(
        web.filter(Query.string().required("q")(it))
      )
    },
  )
}

fun main() {
  routed(Service(JsonStore(Paths.get("./data.json")))).asServer(Undertow(9000)).start()
}
