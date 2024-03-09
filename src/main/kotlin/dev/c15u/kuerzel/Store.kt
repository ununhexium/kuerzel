package dev.c15u.kuerzel

import dev.c15u.kuerzel.persistence.AbbreviationHistory

// TODO: repository / datasource split
interface Store {
  fun add(
    short: String,
    full: String,
    link: String?,
    description: String?,
    tag: List<String>,
  ): AbbreviationHistory

  fun add2(
    short: String,
    full: String,
    link: String?,
    description: String?,
    tag: List<String>,
  ): Result<AbbreviationHistory>

  fun all(): List<AbbreviationHistory>
  fun byId(id: String): AbbreviationHistory?
  fun search(query: String): List<Pair<AbbreviationHistory, Double>>
  fun update(
    id: String,
    short: String,
    full: String,
    link: String,
    description: String,
    tag: List<String>,
  )
}
