package dev.c15u.kuerzel

import dev.c15u.kuerzel.api.AddExample
import dev.c15u.kuerzel.api.dto.CreatedAbbreviation
import dev.c15u.kuerzel.api.dto.ErrorMessage
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
  fun `add example`() {

    val service = ServiceImpl(
      JsonStore(
        Files.createTempFile(null, null),
        uuidGenerator = { UUID.fromString(CreatedAbbreviation.lens(AddExample.Ok.response).id) }
      )
    )

    val api = routed(service)

    val res = api(AddExample.Ok.request)
    res shouldBe AddExample.Ok.response
  }

  @Test
  fun `add failure`() {

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
}
