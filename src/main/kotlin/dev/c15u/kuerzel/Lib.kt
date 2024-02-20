package dev.c15u.kuerzel

import kotlin.math.abs

fun myDistance(x: String, y: String): Int {

  if (x.isEmpty()) {
    return y.length * 10
  }

  if (y.isEmpty()) {
    return x.length * 10
  }

  if (x[0].toString().equals(y[0].toString(), ignoreCase = true)) {
    return myDistance(x.substring(1), y.substring(1))
  } else {

    val swap = if (y.contains(x[0], ignoreCase = true)) {
      val where = y.indexOf(x[0], ignoreCase = true)
      val y2 = y.take(where) + y.drop(where + 1)
      myDistance(x.substring(1), y2) + where
    } else if (x.contains(y[0], ignoreCase = true)) {
      val where = x.indexOf(y[0], ignoreCase = true)
      val x2 = x.take(where) + x.drop(where + 1)
      myDistance(y.substring(1), x2) + where
    } else Int.MAX_VALUE

    // IDEA: adapt this to be proportional to the distance of the keys on the keyboard
    val substitution = myDistance(x.substring(1), y.substring(1)) + 10
    val insertion = myDistance(x, y.substring(1)) + 10
    val deletion = myDistance(x.substring(1), y) + 0

    return listOf(substitution, insertion, deletion, swap).min()
  }
}

fun myDistance2(x: String, y: String): Double {

  val (long, short) = if (x.length > y.length) {
    x to y
  } else {
    y to x
  }

  val diff = long.length - short.length

  val best = (0..diff).minOf {
    val substring = long.drop(it).take(short.length)

    // letters that are at the same place count for 0 difference
    val differentLetters = substring.zip(short).filterNot {
      it.first.equals(it.second, ignoreCase = true)
    }

    val diffScore = 1.0 + score(
      differentLetters.map { it.first.lowercaseChar() }.groupingBy { it }.eachCount(),
      differentLetters.map { it.second.lowercaseChar() }.groupingBy { it }.eachCount(),
    )

    val current = differentLetters.size.toDouble() * diffScore / short.length
    current
  }

  return best
}

fun score(a: Map<Char, Int>, b: Map<Char, Int>): Double {
  if (a.isEmpty() && b.isEmpty()) return 0.0

  val keys = a.keys + b.keys

  val diff = keys.sumOf {
    val c1 = a[it] ?: 0
    val c2 = b[it] ?: 0
    abs(c1 - c2)
  }

  val length = a.values.sum()
  return diff.toDouble() / 2.0 / length
}
