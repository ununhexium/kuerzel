package dev.c15u.kuerzel.persistence

import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Revision(
  // TODO: change the way this is serialized. Too many decimals
  val date: String,
  val abbreviation: Abbreviation
)
