package dev.c15u.kuerzel.persistence

import kotlinx.serialization.Serializable

@Serializable
data class Revision(
  // TODO: change the way this is serialized. Too many decimals
  val date: String,
  val abbreviation: Abbreviation
)
