package dev.c15u.kuerzel

import io.kotest.matchers.shouldBe
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.format.KotlinxSerialization.with
import org.junit.jupiter.api.Test

class ApiTest {
  @Test
  fun `canAddAnAbbreviation`() {
    val payload = Abbreviation("foo", "bar")

    val response = routed(Service())(
      Request(POST, "/api/add").with(
        Abbreviation.lens of payload
      )
    )

    Abbreviation.lens(response).shouldBe(payload)
  }

  @Test
  fun `canListAllTheAbbreviations`() {
    val service = Service()

    val a1 = Abbreviation("foo1", "bar1")
    val a2 = Abbreviation("foo2", "bar2")
    val a3 = Abbreviation("foo3", "bar3")

    service.add(a1)
    service.add(a2)
    service.add(a3)

    val response = routed(service)(
      Request(GET, "/api/all")
    )

    val listLens = Body.auto<Set<Abbreviation>>().toLens()

    listLens(response).shouldBe(setOf(a1, a2, a3))
  }
}
