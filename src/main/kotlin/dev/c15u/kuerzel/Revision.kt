package dev.c15u.kuerzel

import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Revision(
  val date: String,
  val abbreviation: Abbreviation
) {
  companion object {
    val lens = Body.auto<Revision>().toLens()

    fun now(abbreviation: Abbreviation) =
      Revision(
        DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()),
        abbreviation
      )
  }
}
