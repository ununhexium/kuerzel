package dev.c15u.kuerzel

import arrow.core.Either
import arrow.core.Either.Right

class Service {

  private val store = Store()

  fun add(abbreviation: Abbreviation): Either<String, Abbreviation> {
    store.save(abbreviation)
    return Right(abbreviation)
  }

  fun all(): Either<String, List<Abbreviation>> {
    return Right(store.all())
  }

  fun search(query: String): Either<String, List<Abbreviation>> {
    return Right(store.search(query))
  }

  fun search(query: String, distance: (String, String) -> Int): Either<String, List<Abbreviation>> {
    return Right(store.search(query, distance))
  }
}