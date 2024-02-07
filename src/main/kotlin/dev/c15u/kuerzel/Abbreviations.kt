package dev.c15u.kuerzel

import kotlinx.serialization.Serializable

@Serializable
data class Abbreviations(val list: List<Abbreviation>)
