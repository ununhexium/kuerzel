package dev.c15u.kuerzel

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right

class Service(private val store: JsonStore) {

  fun add(short: String, full: String): Right<AbbreviationHistory> {
    return Right(store.add(short, full))
  }

  fun all(): Either<String, List<AbbreviationHistory>> {
    return Right(store.all())
  }

  fun search(query: String): Right<List<Pair<AbbreviationHistory, Double>>> {
    return Right(store.search(query))
  }

  fun byId(id: String): Either<Unit, AbbreviationHistory> {
    return store.byId(id)?.let { Right(it) } ?: Left(Unit)
  }

  fun update(id: String, abbreviation: String, full: String): Either<Unit, Unit> {
    return Right(store.update(id, abbreviation, full))
  }
}
