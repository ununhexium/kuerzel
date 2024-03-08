package dev.c15u.kuerzel

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.file.Files

class JsonStoreTest {

  @Test
  fun `can load an empty file`() {
    val tmp = Files.createTempFile(null, null)
    JsonStore(tmp).load()
  }

  @Test
  fun `can save an empty file`() {
    val tmp = Files.createTempFile(null, null)
    val s = JsonStore(tmp)
    s.save()
  }

  @Test
  fun `create read update`() {
    val tmp = Files.createTempFile(null, null)
    val s = JsonStore(tmp)
    val abbr = s.add(
      "short",
      "full",
      "link",
      "description",
      listOf("tag1", "tag2"),
    )

    s.save()
    val result1 = s.byId(abbr.id)
    result1?.mostRecent()?.abbreviation shouldBe Abbreviation(
      "short",
      "full",
      "link",
      "description",
      listOf("tag1", "tag2"),
    )

    s.update(
      abbr.id,
      "short+",
      "full+",
      "link+",
      "description+",
      listOf("tag1+", "tag2+"),
    )

    val result2 = s.byId(abbr.id)
    result2?.mostRecent()?.abbreviation shouldBe Abbreviation(
      "short+",
      "full+",
      "link+",
      "description+",
      listOf("tag1+", "tag2+"),
    )
  }
  
}
