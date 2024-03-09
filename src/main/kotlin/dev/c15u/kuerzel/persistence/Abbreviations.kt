package dev.c15u.kuerzel.persistence

import dev.c15u.kuerzel.persistence.AbbreviationHistory
import kotlinx.serialization.Serializable

@Serializable
data class Abbreviations(val list: List<AbbreviationHistory>)
