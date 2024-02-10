package dev.c15u.kuerzel

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class JsonStore(val location: Path) : Store {

  companion object {
    val json = Json {
      prettyPrint = true
      prettyPrintIndent = "  "
    }
  }

  private val data: MutableList<AbbreviationHistory> = load()

  override fun all(): List<AbbreviationHistory> =
    data.toList()

  override fun byId(id: String): AbbreviationHistory? {
    return data.firstOrNull { it.id == id }
  }

  override fun load(): MutableList<AbbreviationHistory> {
    return if (location.toFile().exists()) {
      Json.decodeFromString<Abbreviations>(location.toFile().readText()).list.toMutableList()
    } else {
      mutableListOf()
    }
  }

  override fun save(abbreviationHistory: AbbreviationHistory) {
    synchronized(this) {
      data.add(abbreviationHistory)
      location.toFile().writer(Charsets.UTF_8).use {
        it.write(
          json.encodeToString(Abbreviations(data))
        )
      }
    }
  }

  override fun search(query: String): List<Pair<AbbreviationHistory, Double>> {
    val exact = data.filter {
      val a = it.mostRecent().abbreviation
      a.short.contains(query, ignoreCase = true) ||
          a.full.contains(query, ignoreCase = true)
    }

    val byDistance = data
      .asSequence()
      .filter { it !in exact }
      .map { it to myDistance(it.mostRecent().abbreviation.short, query) }
      .filter { it.second < 100 }
      .sortedBy { it.second }
      .map { it.first to it.second.toDouble() }
      .toList()

    return exact.map { it to 0.0 } + byDistance
  }

  override fun update(id: String, abbreviation: String, full: String) {
    TODO("Introduce versioning")
  }

  override fun add(short: String, full: String): AbbreviationHistory {
    val element = AbbreviationHistory(
      id = UUID.randomUUID().toString(),
      listOf(
        Revision(
          date = nowString(),
          abbreviation = Abbreviation(short, full)
        )
      )
    )

    save(element)

    return element
  }

  private fun nowString(): String {
    return DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())
  }
}
