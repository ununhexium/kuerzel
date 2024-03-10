package dev.c15u.kuerzel

import arrow.core.Either
import dev.c15u.kuerzel.persistence.Abbreviation
import dev.c15u.kuerzel.persistence.AbbreviationHistory

// TODO: split model/storage?
interface Service {
  fun add(
    short: String,
    full: String,
    link: String?,
    description: String?,
    tag: List<String>,
  ): Either<String, AbbreviationHistory>

  fun add2(
    short: String,
    full: String,
    link: String?,
    description: String?,
    tag: List<String>,
  ): Result<AbbreviationHistory>

  fun all(): Either<String, List<AbbreviationHistory>>

  fun all2(): Result<List<AbbreviationHistory>>

  fun search(query: String): Either.Right<List<Pair<AbbreviationHistory, Double>>>

  fun byId(id: String): Either<Unit, AbbreviationHistory>

  fun update(
    id: String,
    short: String,
    full: String,
    link: String,
    description: String,
    tag: List<String>,
  ): Either<Unit, Unit>

  fun allCsv(): Either.Right<String>
}
