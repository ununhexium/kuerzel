package dev.c15u.kuerzel.api.example

import org.http4k.core.Status

interface Example {
  val examples: Map<Status, List<ReqRes>>
}
