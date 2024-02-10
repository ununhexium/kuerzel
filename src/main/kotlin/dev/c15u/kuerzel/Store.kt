package dev.c15u.kuerzel

// TODO: repository / datasource
interface Store {
  fun add(short: String, full: String): AbbreviationHistory
  fun all(): List<AbbreviationHistory>
  fun byId(id: String): AbbreviationHistory?
  fun load(): MutableList<AbbreviationHistory>
  fun save(abbreviationHistory: AbbreviationHistory)
  fun search(query: String): List<Pair<AbbreviationHistory, Double>>
  fun update(id: String, abbreviation: String, full: String)
}
