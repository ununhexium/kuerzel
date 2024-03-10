package dev.c15u.kuerzel.api

import org.http4k.contract.security.BasicAuthSecurity
import org.http4k.core.Credentials

// TODO: configurable basic auth
val mySecurity = BasicAuthSecurity("api", Credentials("user", "pass"))
