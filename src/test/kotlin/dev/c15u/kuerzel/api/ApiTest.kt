package dev.c15u.kuerzel.api

import dev.c15u.kuerzel.*
import dev.c15u.kuerzel.api.dto.Abbreviation
import dev.c15u.kuerzel.api.dto.Abbreviations
import dev.c15u.kuerzel.api.dto.ErrorMessage
import dev.c15u.kuerzel.persistence.AbbreviationHistory
import dev.c15u.kuerzel.persistence.Revision
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.util.*

class ApiTest {
  @Test
  fun `add abbreviations example`() {

    val service = ServiceImpl(
      JsonStore(
        Files.createTempFile(null, null),
        uuidGenerator = { UUID.fromString(Abbreviation.lens(AddExample.Ok.response).id) }
      )
    )

    val api = routed(service)

    val res = api(AddExample.Ok.request)
    res shouldBe AddExample.Ok.response
  }

  @Test
  fun `add abbreviations failure`() {

    val store = mockk<Store>()
    every {
      store.add2(
        any(),
        any(),
        any(),
        any(),
        any()
      )
    } returns Result.failure(IOException("Out of disk space"))

    val service = ServiceImpl(store)

    val api = routed(service)

    val res = api(AddExample.Ok.request)
    res shouldBe Response(Status.INTERNAL_SERVER_ERROR).with(
      ErrorMessage.lens of ErrorMessage("Out of disk space")
    )
  }

  @Test
  fun `all abbreviations as json example`() {

    val service = mockk<Service>()
    every { service.all2() } returns
        Result.success(
          listOf(
            AbbreviationHistory(
              id = "01234567-0123-0123-0123-0123456789ab",
              listOf(
                Revision(
                  "2024-01-01T00:00:00",
                  dev.c15u.kuerzel.persistence.Abbreviation(
                    short = "LASER",
                    full = "Light Amplification by Stimulated Emission of Radiation",
                    link = "https://en.wikipedia.org/wiki/Laser",
                    description = "A kind of remote control that works on cats",
                    tags = listOf("toy", "pretty"),
                  )
                )
              )
            ),
            AbbreviationHistory(
              id = "98765432-9876-9876-9876-9876543210fe",
              listOf(
                Revision(
                  "2024-01-01T00:00:00",
                  dev.c15u.kuerzel.persistence.Abbreviation(
                    short = "Pulsar",
                    full = "Pulsating radio source",
                    link = null,
                    description = null,
                    tags = listOf(),
                  )
                )
              )
            )
          )
        )

    val api = routed(service)

    val res = api(AllExample.Ok.request)
    res shouldBe AllExample.Ok.response
  }

  @Test
  fun `all abbreviations as CSV example`() {

    val service = mockk<Service>()
    every { service.all2() } returns
        Result.success(
          listOf(
            AbbreviationHistory(
              id = "01234567-0123-0123-0123-0123456789ab",
              listOf(
                Revision(
                  "2024-01-01T00:00:00",
                  dev.c15u.kuerzel.persistence.Abbreviation(
                    short = "LASER",
                    full = "Light Amplification by Stimulated Emission of Radiation",
                    link = "https://en.wikipedia.org/wiki/Laser",
                    description = "A kind of remote control that works on cats",
                    tags = listOf("toy", "pretty"),
                  )
                )
              )
            ),
            AbbreviationHistory(
              id = "98765432-9876-9876-9876-9876543210fe",
              listOf(
                Revision(
                  "2024-01-01T00:00:00",
                  dev.c15u.kuerzel.persistence.Abbreviation(
                    short = "Pulsar",
                    full = "Pulsating radio source",
                    link = null,
                    description = null,
                    tags = listOf(),
                  )
                )
              )
            )
          )
        )

    val api = routed(service)

    val res = api(AllExample.Ok.request2)
    res shouldBe AllExample.Ok.response2
  }

}
