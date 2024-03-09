package dev.c15u.kuerzel.api.dto

import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto
import java.util.*

@Serializable
data class CreatedAbbreviation(
  val id: String,
  val short: String,
  val full: String,
  val link: String? = null,
  val description: String? = null,
  val tags: List<String> = listOf(),
) {
  companion object {
    val lens = Body.auto<CreatedAbbreviation>().toLens()
  }
}
