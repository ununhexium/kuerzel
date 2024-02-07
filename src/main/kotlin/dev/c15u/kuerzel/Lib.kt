package dev.c15u.kuerzel

import kotlin.math.min

/**
 * From
 * https://en.wikipedia.org/wiki/Levenshtein_distance
 *
 * Iterative with two matrix rows
 */
fun levenshteinDistance(s: String, t: String): Int {
  var v0 = MutableList(t.length + 1) { it }
  var v1 = MutableList(t.length + 1) { it }

  for (i in s.indices) {
    v1[0] = i + 1

    for (j in t.indices) {
      val deletionCost = v0[j + 1] + 1
      val insertionCost = v1[j] + 1
      val substitutionCost = if (s[i] == t[j]) v0[j] else v0[j] + 1

      v1[j + 1] = min(deletionCost, min(insertionCost, substitutionCost))
    }
    v0 = v1
  }

  return v0[t.length]

  /*
  function LevenshteinDistance(char s[0..m-1], char t[0..n-1]):
    declare int v0[n + 1]
    declare int v1[n + 1]

    for i from 0 to n:
        v0[i] = i

    for i from 0 to m - 1:
        v1[0] = i + 1

        for j from 0 to n - 1:
            // calculating costs for A[i + 1][j + 1]
            deletionCost := v0[j + 1] + 1
            insertionCost := v1[j] + 1
            if s[i] = t[j]:
                substitutionCost := v0[j]
            else:
                substitutionCost := v0[j] + 1

            v1[j + 1] := minimum(deletionCost, insertionCost, substitutionCost)

        // copy v1 (current row) to v0 (previous row) for next iteration
        // since data in v1 is always invalidated, a swap without copy could be more efficient
        swap v0 with v1
    // after the last swap, the results of v1 are now in v0
    return v0[n]
   */
}