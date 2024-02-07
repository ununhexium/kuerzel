package dev.c15u.kuerzel

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.math.min

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

  fun all(): List<Abbreviation> =
    data.toList()

  fun search(query: String): List<Abbreviation> {
    return data.filter {
      it.abbreviation.contains(query, ignoreCase = true) ||
          it.full.contains(query, ignoreCase = true)
    }
  }

  fun search(query: String, distance: (String, String) -> Int): List<Abbreviation> {
    return data.filter {
      min(
        distance(it.abbreviation, query),
        distance(it.full, query)
      ) < 10
    }.sortedBy {
      min(
        distance(it.abbreviation, query),
        distance(it.full, query)
      )
    }
  }
}
