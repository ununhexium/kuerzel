package dev.c15u.kuerzel.api

import dev.c15u.kuerzel.Service
import dev.c15u.kuerzel.api.dto.Abbreviation
import dev.c15u.kuerzel.api.dto.ErrorMessage
import dev.c15u.kuerzel.api.dto.NewAbbreviation
import org.http4k.contract.ContractRoute
import org.http4k.contract.HttpMessageMeta
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.format.KotlinxSerialization.auto

object AddExample {

  object Ok {

    val request =
      Request(
        POST,
        "/api/add"
      ).with(
        NewAbbreviation.lens of NewAbbreviation(
          short = "LASER",
          full = "Light Amplification by Stimulated Emission of Radiation",
          link = "https://en.wikipedia.org/wiki/Laser",
          description = "A kind of remote control that works on cats",
          tags = listOf("toy", "pretty")
        )
      )

    val response = Response(OK).with(
      Abbreviation.lens of Abbreviation(
        id = "01234567-0123-0123-0123-0123456789ab",
        short = "LASER",
        full = "Light Amplification by Stimulated Emission of Radiation",
        link = "https://en.wikipedia.org/wiki/Laser",
        description = "A kind of remote control that works on cats",
        tags = listOf("toy", "pretty")
      )
    )
  }

}

fun Add(service: Service): ContractRoute {
  fun handler(): HttpHandler =
    { req ->
      val abbr = NewAbbreviation.lens(req)

      service.add2(abbr.short, abbr.full, abbr.link, abbr.description, abbr.tags).fold(
        { history ->
          val createdAbbr = history.mostRecent().abbreviation
          Response(OK).with(
            Body.auto<Abbreviation>().toLens() of Abbreviation(
              history.id,
              createdAbbr.short,
              createdAbbr.full,
              createdAbbr.link,
              createdAbbr.description,
              createdAbbr.tags,
            )
          )
        },
        {
          Response(Status.INTERNAL_SERVER_ERROR).with(
            ErrorMessage.lens of ErrorMessage.from(it)
          )
        }
      )
    }

  val spec = "/api/add" meta {
    summary = "Add a new abbreviation"
    description = """
        |Adds a new abbreviation to the list.
        |Duplicates are allowed (e.g. AC is either Air Conditioning or Alternative Current).
        |Returns the newly created abbreviation as it has been stored in the database.
      """.trimMargin()
    produces += ContentType.APPLICATION_JSON
    operationId = "add"

    receiving(
      HttpMessageMeta(
        AddExample.Ok.request,
        "Add a new abbreviation.",
        "add",
        null,
      )
    )

    returning(
      HttpMessageMeta(
        AddExample.Ok.response,
        "Created a new abbreviation",
        "addResOk",
        null,
      )
    )
  } bindContract POST

  return spec to ::handler
}
