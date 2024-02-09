package dev.c15u.kuerzel

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
    val deletion = myDistance(x.substring(1), y) + 10

    return listOf(substitution, insertion, deletion, swap).min()
  }
}
