package dev.c15u.kuerzel.api.dto

import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto

@Serializable
data class ErrorMessage(val message: String) {

  companion object {
    val lens = Body.auto<ErrorMessage>().toLens()

    fun from(throwable: Throwable) =
      from(throwable.message)

    fun from(message: String?) =
      ErrorMessage(message ?: "No error message specified.")
  }
}
