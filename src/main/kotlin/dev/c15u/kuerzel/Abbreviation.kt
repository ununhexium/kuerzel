package dev.c15u.kuerzel

import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto

@Serializable
data class Abbreviation(
  val short: String,
  val full: String,
  val link: String = "",
  val description: String = "",
  val tags: List<String> = listOf(),
) {
  companion object {
    val lens = Body.auto<Abbreviation>().toLens()
  }
}
