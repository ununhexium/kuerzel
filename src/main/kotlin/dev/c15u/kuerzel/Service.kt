package dev.c15u.kuerzel

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.opencsv.CSVWriter
import java.io.StringWriter


class Service(private val store: JsonStore) {

  fun add(
    short: String,
    full: String,
    link: String,
    description: String,
    tag: List<String>,
  ): Right<AbbreviationHistory> {
    return Right(store.add(short, full, link, description, tag))
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

  fun update(
    id: String,
    short: String,
    full: String,
    link: String,
    description: String,
    tag: List<String>,
  ): Either<Unit, Unit> {
    return Right(store.update(id, short, full, link, description, tag))
  }

  fun allCsv(): Right<String> {
    val all = store.all()

    val sw = StringWriter()

    CSVWriter(sw).use { writer ->
      writer.writeNext(arrayOf("short", "full", "link", "description", "tags"))

      all.forEach {
        val a = it.mostRecent().abbreviation
        writer.writeNext(
          arrayOf(a.short, a.full, a.link, a.description) + a.tags
        )
      }
    }

    return Right(sw.toString())
  }
}
