package dev.c15u.kuerzel.api

import com.opencsv.CSVWriter
import dev.c15u.kuerzel.Service
import dev.c15u.kuerzel.api.dto.Abbreviation
import dev.c15u.kuerzel.api.dto.Abbreviations
import dev.c15u.kuerzel.api.dto.ErrorMessage
import dev.c15u.kuerzel.persistence.AbbreviationHistory
import org.http4k.contract.ContractRoute
import org.http4k.contract.HttpMessageMeta
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Query
import org.http4k.lens.string
import java.io.StringWriter

object AllExample {

  object Ok {

    val allAbbreviations = listOf(
      Abbreviation(
        id = "01234567-0123-0123-0123-0123456789ab",
        short = "LASER",
        full = "Light Amplification by Stimulated Emission of Radiation",
        link = "https://en.wikipedia.org/wiki/Laser",
        description = "A kind of remote control that works on cats",
        tags = listOf("toy", "pretty")
      ),
      Abbreviation(
        id = "98765432-9876-9876-9876-9876543210fe",
        short = "Pulsar",
        full = "Pulsating radio source",
        link = null,
        description = null,
        tags = listOf()
      )
    )

    val request =
      Request(
        Method.GET,
        "/api/abbreviations"
      ).header("Accept", "application/json")

    val response = Response(OK)
      .header("Content-Type", "application/json")
      .with(Abbreviations.lens of Abbreviations(allAbbreviations))

    val request2a =
      Request(Method.GET, "/api/abbreviations")
        .header("Accept", "text/csv")

    val request2b =
      Request(Method.GET, "/api/abbreviations")
        .query("format", "csv")

    val response2 =
      Response(OK)
        .header("Content-Type", "text/csv")
        .body(
          """
            |"short","full","link","description","tags"
            |"LASER","Light Amplification by Stimulated Emission of Radiation","https://en.wikipedia.org/wiki/Laser","A kind of remote control that works on cats","toy pretty"
            |"Pulsar","Pulsating radio source",,,""
            |
          """.trimMargin()
        )
  }
}

fun All(service: Service): ContractRoute {
  val foldAsJson = { all: List<AbbreviationHistory> ->
    Response(OK).with(
      Body.auto<Abbreviations>().toLens() of Abbreviations(
        all.map {
          Abbreviation(
            it.id,
            it.mostRecent().abbreviation.short,
            it.mostRecent().abbreviation.full,
            it.mostRecent().abbreviation.link,
            it.mostRecent().abbreviation.description,
            it.mostRecent().abbreviation.tags,
          )
        }
      )
    )
  }

  val foldAsCsv = { all: List<AbbreviationHistory> ->
    val sw = StringWriter()

    CSVWriter(sw).use { writer ->
      writer.writeNext(arrayOf("short", "full", "link", "description", "tags"))

      all.forEach {
        val a = it.mostRecent().abbreviation
        writer.writeNext(
          arrayOf(a.short, a.full, a.link, a.description) + a.tags.joinToString(" ")
        )
      }
    }

    Response(OK)
      .header("Content-Type", "text/csv")
      .body(sw.toString())
  }

  val formatQuery = Query.optional("format")

  fun handler(): HttpHandler =
    { req ->
      service.all2().fold(
        {
          if (req.header("Accept") == "text/csv" ||
            formatQuery(req).equals("csv", ignoreCase = true)) {
            foldAsCsv(it)
          } else {
            foldAsJson(it)
          }
        },
        {
          Response(Status.INTERNAL_SERVER_ERROR).with(
            ErrorMessage.lens of ErrorMessage.from(it)
          )
        }
      )
    }

  val spec = "/abbreviations" meta {
    summary = "List all the abbreviations"
    description = """
        |Lists all known abbreviations.
      """.trimMargin()
    produces += ContentType.APPLICATION_JSON
    operationId = "all"

    queries += formatQuery

    receiving(
      HttpMessageMeta(
        AddExample.Ok.request,
        "List all abbreviations.",
        "allReq",
        null,
      )
    )

    returning(
      HttpMessageMeta(
        AddExample.Ok.response,
        "A list of all the abbreviations",
        "allRes",
        null,
      )
    )
  } bindContract Method.GET

  return spec to ::handler
}
