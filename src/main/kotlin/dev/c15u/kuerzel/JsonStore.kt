package dev.c15u.kuerzel

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path

class JsonStore(val location: Path) : Store {

  private val data: MutableList<Abbreviation> = load()

  private fun load(): MutableList<Abbreviation> {
    return if (location.toFile().exists()) {
      Json.decodeFromString<Abbreviations>(location.toFile().readText()).list.toMutableList()
    } else {
      mutableListOf()
    }
  }

  fun save(abbreviation: Abbreviation) {
    data.add(abbreviation)
    location.toFile().writer(Charsets.UTF_8).use {
      it.write(
        Json {
          prettyPrint = true
          prettyPrintIndent = "  "
        }.encodeToString(Abbreviations(data))
      )
    }
  }

  fun all(): List<Pair<Abbreviation, Double>> =
    data.toList().map { it to 0.0 }

  fun search(query: String): List<Pair<Abbreviation, Double>> {
    val exact = data
      .filter {
        it.abbreviation.contains(query, ignoreCase = true) ||
            it.full.contains(query, ignoreCase = true)
      }

    val byDistance = data
      .asSequence()
      .filter { it !in exact }
      .map { it to myDistance(it.abbreviation, query) }
      .filter { it.second < 100 }
      .sortedBy { it.second }
      .map { it.first to it.second.toDouble() }
      .toList()

    return exact.map { it to 0.0 } + byDistance
  }
}
