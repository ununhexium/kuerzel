package dev.c15u.kuerzel

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
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

  fun save() {
    synchronized(this) {
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
      .filter { it.second < Config.MAX_DIFFERENCE }
      .sortedBy { it.second }
      .map { it.first to it.second.toDouble() }
      .toList()

    return exact.map { it to 0.0 } + byDistance
  }

  override fun update(id: String, short: String, full: String) {
    val index = data.indexOfFirst { it.id == id }
    val byIndex = data.removeAt(index)
    val new = byIndex.copy(
      revisions = byIndex.revisions + listOf(Revision.now(Abbreviation(short, full)))
    )
    data.add(0, new)
    save()
  }

  override fun add(short: String, full: String): AbbreviationHistory {
    val element = AbbreviationHistory(
      id = UUID.randomUUID().toString(),
      listOf(
        Revision.now(Abbreviation(short, full))
      )
    )

    save()

    return element
  }
}
