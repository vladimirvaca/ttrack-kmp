package com.rvladimir.ttrack.core.ui.extensions

/**
 * Formats this value (in seconds) to a `MM:SS` string representation.
 *
 * Examples:
 * - `90.toTimeString()` → `"01:30"`
 * - `3661.toTimeString()` → `"61:01"`
 */
fun Int.toTimeString(): String {
    val m = this / 60
    val s = this % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
