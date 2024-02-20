package dev.c15u.kuerzel

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LibKtTest {
  @Test
  fun `sql SQL 0`() {
    val distance = myDistance("sql", "SQL")
    assertThat(distance).isEqualTo(0)
  }

  @Test
  fun `sql slq 1`() {
    val distance = myDistance("sql", "slq")
    assertThat(distance).isEqualTo(1)
  }

  @Test
  fun `lqs sql 3`() {
    val distance = myDistance("sql", "lqs")
    assertThat(distance).isEqualTo(3)
  }

  @Test
  fun `a ab 10`() {
    val distance = myDistance("a", "ab")
    assertThat(distance).isEqualTo(10)
  }

  @Test
  fun `a ba 11`() {
    val distance = myDistance("a", "ab")
    assertThat(distance).isEqualTo(10)
  }

  @Test
  fun `ax ab 10`() {
    val distance = myDistance("ax", "ab")
    assertThat(distance).isEqualTo(10)
  }

  @Test
  fun `s WE 20`() {
    val distance = myDistance("s", "WE")
    assertThat(distance).isEqualTo(20)
  }

  @Test
  fun `distance between 2 identical strings`() {
    assertThat(
      myDistance2("aaa", "aaa")
    ).isEqualTo(
      0.0
    )
  }

  @Test
  fun `distance between completely different strings`() {
    assertThat(
      myDistance2("abc", "xyz")
    ).isEqualTo(
      2.0
    )
  }

  @Test
  fun `same letter but different order`() {
    assertThat(
      myDistance2("abc", "bca")
    ).isEqualTo(
      1.0
    )
  }

  @Test
  fun `if substring is found then distance is 0`() {
    assertThat(
      myDistance2("cde", "abcdefg")
    ).isEqualTo(
      0.0
    )
  }

  @Test
  fun `if similar substring is found then distance is 1`() {
    assertThat(
      myDistance2("efcd", "abcdefgh")
    ).isEqualTo(
      1.0
    )
  }

  @Test
  fun `laser - lsq`() {
    assertThat(
      myDistance2("Laser", "lsq")
    ).isCloseTo(
      0.3333,
      0.01
    )
  }
}