package dev.c15u.kuerzel.api.dto

import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto

@Serializable
data class Abbreviations(val abbreviations: List<Abbreviation>) {
  companion object {
    val lens = Body.auto<Abbreviations>().toLens()
  }
}
