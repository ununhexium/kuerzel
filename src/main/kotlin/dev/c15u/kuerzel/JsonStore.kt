package dev.c15u.kuerzel

import dev.c15u.kuerzel.persistence.Abbreviation
import dev.c15u.kuerzel.persistence.AbbreviationHistory
import dev.c15u.kuerzel.persistence.Abbreviations
import dev.c15u.kuerzel.persistence.Revision
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import java.time.Clock
import java.time.LocalDateTime
import java.util.*


class JsonStore(
  private val location: Path,
  private val clock: Clock = Clock.systemUTC(),
  private val uuidGenerator: () -> UUID = { UUID.randomUUID() },
) : Store {

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

  fun load(): MutableList<AbbreviationHistory> {
    val file = location.toFile()
    ensureDirectoryExists(file)
    if (!file.exists() || file.readText().isEmpty()) {
      return mutableListOf()
    } else {
      return Json.decodeFromString<Abbreviations>(file.readText()).list.toMutableList()
    }
  }

  fun save() {
    synchronized(this) {
      val file = location.toFile()
      ensureDirectoryExists(file)
      file.writer(Charsets.UTF_8).use {
        it.write(
          json.encodeToString(Abbreviations(data))
        )
      }
    }
  }

  private fun ensureDirectoryExists(file: File) {
    if (!file.parentFile.exists()) {
      file.parentFile.mkdirs()
    }
  }

  override fun search(query: String): List<Pair<AbbreviationHistory, Double>> {
    val byDistance = data
      .asSequence()
      .map {
        val a = it.mostRecent().abbreviation
        val fields = listOf(a.short, a.full, a.description) + a.tags
        it to (fields.mapNotNull { it }.map { f -> myDistance2(f, query) }.filterNot { it.isNaN() }
          .minOrNull()
          ?: 2.0)
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
        Revision(
          LocalDateTime.now(clock).toString(),
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
    link: String?,
    description: String?,
    tag: List<String>
  ): AbbreviationHistory {
    val element = AbbreviationHistory(
      id = uuidGenerator().toString(),
      revisions = listOf(
        Revision(
          LocalDateTime.now(clock).toString(),
          Abbreviation(short, full, link, description, tag),
        )
      )
    )

    data.add(element)

    save()

    return element
  }

  override fun add2(
    short: String,
    full: String,
    link: String?,
    description: String?,
    tag: List<String>
  ): Result<AbbreviationHistory> =
    Result.success(add(short, full, link, description, tag))
}
