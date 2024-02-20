package dev.c15u.kuerzel

// TODO: repository / datasource split
interface Store {
  fun add(
    short: String,
    full: String,
    link: String,
    description: String,
    tag: List<String>,
  ): AbbreviationHistory
  fun all(): List<AbbreviationHistory>
  fun byId(id: String): AbbreviationHistory?
  fun load(): MutableList<AbbreviationHistory>
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
