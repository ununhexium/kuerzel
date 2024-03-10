package dev.c15u.kuerzel.api

import dev.c15u.kuerzel.Service
import dev.c15u.kuerzel.persistence.AbbreviationHistory
import org.http4k.contract.security.BasicAuthSecurity
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

// TODO: configurable basic auth
val mySecurity = BasicAuthSecurity("api", Credentials("user", "pass"))

object Api {
  operator fun invoke(service: Service) : RoutingHttpHandler =
    routes(
      // TODO: move to POST /api/abbreviation
      "/add" bind Add(service),
      "/abbreviations" bind All(service),
    )
}
