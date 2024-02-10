package dev.c15u.kuerzel

import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto


@Serializable
data class AbbreviationHistory(
  val id: String,
  val revisions: List<Revision>
) {
  companion object {
    val lens = Body.auto<AbbreviationHistory>().toLens()
  }

  fun mostRecent(): Revision {
    return revisions.asSequence().sortedByDescending { it.date }.first()
  }
}
