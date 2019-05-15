package party.rezruel.servermonitor.helpers

import party.rezruel.servermonitor.enums.TimeUnit

val UNITS = mapOf(
        's' to fun(v: String): Long = v.toLong() * TimeUnit.SECONDS.value,
        'm' to fun(v: String): Long = v.toLong() * TimeUnit.MINUTES.value,
        'h' to fun(v: String): Long = v.toLong() * TimeUnit.HOURS.value,
        'd' to fun(v: String): Long = v.toLong() * TimeUnit.DAYS.value,
        'w' to fun(v: String): Long = v.toLong() * TimeUnit.WEEKS.value
)

fun parseDuration(raw: String): Long {
    var value: Long = 0
    var digits = ""

    for (char in raw) {
        if (char.isDigit()) {
            digits += char
            continue
        }

        if (char !in UNITS || digits.isBlank()) {
            throw IllegalArgumentException("Invalid duration.\n$char is not a valid unit")
        }

        value += UNITS[char]?.invoke(digits) ?: 0
        digits = ""
    }

    return value
}