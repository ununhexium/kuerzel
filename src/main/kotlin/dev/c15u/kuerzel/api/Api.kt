package dev.c15u.kuerzel.api

import dev.c15u.kuerzel.Service
import org.http4k.contract.security.BasicAuthSecurity
import org.http4k.core.Credentials
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

// TODO: configurable basic auth
val mySecurity = BasicAuthSecurity("api", Credentials("user", "pass"))

object Api {
  operator fun invoke(service: Service) : RoutingHttpHandler =
    routes(
      "/api/add" bind Add(service)
    )
}
