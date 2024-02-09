package dev.c15u.kuerzel

import arrow.core.Either
import arrow.core.Either.Right
import java.nio.file.Paths

class Service {

  private val store = JsonStore(Paths.get("./data.json"))

  fun add(abbreviation: Abbreviation): Either<String, Abbreviation> {
    store.save(abbreviation)
    return Right(abbreviation)
  }

  fun all(): Either<String, List<Pair<Abbreviation, Double>>> {
    return Right(store.all())
  }

  fun search(query: String): Right<List<Pair<Abbreviation, Double>>> {
    return Right(store.search(query))
  }
}
