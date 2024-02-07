package dev.c15u.kuerzel

import kotlin.math.min

class Store {

  private val data = mutableListOf<Abbreviation>(
    // TODO delete later
    Abbreviation("FUBAR", "Fucked up beyond any repair"),
    Abbreviation("Laser", "light amplification by stimulated emission of radiation"),
    Abbreviation("UNESCO", "United Nations Educational, Scientific and Cultural Organization"),
  )

  fun save(abbreviation: Abbreviation) {
    data.add(abbreviation)
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
