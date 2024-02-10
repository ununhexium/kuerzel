plugins {
  kotlin("jvm") version "1.9.21"
  kotlin("plugin.serialization") version "1.9.21"
}

repositories {
  mavenCentral()
}

dependencies {

  implementation("com.opencsv:opencsv:5.7.1")

  implementation("com.willowtreeapps.assertk:assertk:0.28.0")

  val arrow = "1.2.0"
  implementation("io.arrow-kt:arrow-core:${arrow}")

  implementation(platform("org.http4k:http4k-bom:5.13.2.0"))
  implementation("org.http4k:http4k-core")
  implementation("org.http4k:http4k-server-undertow")
  implementation("org.http4k:http4k-format-kotlinx-serialization")
  implementation("org.http4k:http4k-multipart")

  //Fill this in with the version of kotlinx in use in your project
  val kotlinxHtmlVersion = "0.11.0"
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
  implementation("org.jetbrains.kotlinx:kotlinx-html:${kotlinxHtmlVersion}")

  // TEST

  val kotest = "5.8.0"
  testImplementation("io.kotest:kotest-runner-junit5:$kotest")
  testImplementation("io.kotest:kotest-assertions-core:$kotest")
  testImplementation("io.kotest.extensions:kotest-assertions-arrow-jvm:1.4.0")

  testImplementation("org.http4k:http4k-client-apache")

  val jupiter = "5.8.1"
  testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiter}")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiter}")
}

sourceSets.main {
  java.srcDirs("src/main/kotlin")
}

sourceSets.test {
  java.srcDirs("src/test/kotlin")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
