package dev.c15u.kuerzel

import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto


@Serializable
data class Abbreviation(
  val abbreviation: String,
  val full: String,
) {
  companion object {
    val lens = Body.auto<Abbreviation>().toLens()
  }
}
