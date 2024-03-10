package dev.c15u.kuerzel.api.dto

data class AbbreviationsCsv(
  val abbreviations: List<Abbreviation>
) {
  fun toCsv(): String {
    TODO("CSV")
  }
}