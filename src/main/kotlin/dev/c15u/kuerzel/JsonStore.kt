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
    val byDistance = data
      .asSequence()
      .map {
        val a = it.mostRecent().abbreviation
        val fields = listOf(a.short, a.full, a.description) + a.tags
        it to (fields.map { f -> myDistance2(f, query) }.filterNot { it.isNaN() }.minOrNull() ?: 2.0)
      }
      .sortedBy { it.second }
      .toList()

    return byDistance
  }

  override fun update(
    id: String,
    short: String,
    full: String,
    link: String,
    description: String,
    tag: List<String>
  ) {
    val index = data.indexOfFirst { it.id == id }
    val byIndex = data.removeAt(index)
    val new = byIndex.copy(
      revisions = byIndex.revisions + listOf(
        Revision.now(
          Abbreviation(short, full, link, description, tag)
        )
      )
    )
    data.add(0, new)
    save()
  }

  override fun add(
    short: String,
    full: String,
    link: String,
    description: String,
    tag: List<String>
  ): AbbreviationHistory {
    val element = AbbreviationHistory(
      id = UUID.randomUUID().toString(),
      listOf(
        Revision.now(Abbreviation(short, full, link, description, tag))
      )
    )

    data.add(element)

    save()

    return element
  }
}
