package dev.c15u.kuerzel

import dev.c15u.kuerzel.api.Add
import dev.c15u.kuerzel.api.All
import dev.c15u.kuerzel.api.mySecurity
import dev.c15u.kuerzel.persistence.AbbreviationHistory
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.ApiServer
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.format.Argo
import org.http4k.lens.Query
import org.http4k.lens.string
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Undertow
import org.http4k.server.asServer
import java.nio.file.Paths

val routed = { service: Service ->
  val web = Web(service)

  routes(
    "/api" bind contract {
      renderer = OpenApi3(
        ApiInfo("Kürzel", "0.1"),
        Argo,
        servers = listOf(ApiServer(Uri.of("http://localhost:8000"), "the greatest server"))
      )
      descriptionPath = "/openapi.json"
      security = mySecurity

      routes += Add(service)
      routes += All(service)
    },
    "/static" bind static(ResourceLoader.Classpath("/static")),
    "/index.html" bind GET to {
      val query = it.query("q") ?: ""
      Response(OK).body(web.index(query))
    },
    "/web/add" bind POST to {
      val short = it.form("short") ?: ""
      val full = it.form("full") ?: ""
      val link = it.form("link") ?: ""
      val description = it.form("description") ?: ""
      val tag = it.form("tags")?.split(", ") ?: listOf()
      service.add(short, full, link, description, tag)
        .fold({ Response(BAD_REQUEST) }, { Response(OK).with(AbbreviationHistory.lens of it) })
      Response(OK).body(web.index(short))
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
    "/web/edit" bind POST to {
      val id = it.form("id") ?: ""
      val short = it.form("short") ?: ""
      val full = it.form("full") ?: ""
      val link = it.form("link") ?: ""
      val description = it.form("description") ?: ""
      val tag = it.form("tags")?.split(",") ?: listOf()
      service.update(id, short, full, link, description, tag)
        .fold(
          { Response(OK).body("") },
          { Response(OK).body("") },
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
  val location = Paths.get(System.getenv("KUERZEL_STORAGE") ?: "./data.json")
  routed(ServiceImpl(JsonStore(location))).asServer(Undertow(9000)).start()
}
