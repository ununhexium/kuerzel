package dev.c15u.kuerzel

class Store {

  private val data = mutableListOf<Abbreviation>(
    // TODO delete later
    Abbreviation("fubar", "Fucked up beyond any repair"),
    Abbreviation("Laser", "light amplification by stimulated emission of radiation"),
  )

  fun save(abbreviation: Abbreviation) {
    data.add(abbreviation)
  }

  fun all(): List<Abbreviation> =
    data.toList()
}
