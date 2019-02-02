package party.rezruel.servermonitor.helpers

val UNITS = mapOf(
    's' to fun(v: String): Long = v.toLong() * 1000,
    'm' to fun(v: String): Long = v.toLong() * 1000 * 60,
    'h' to fun(v: String): Long = v.toLong() * 1000 * 60 * 60,
    'd' to fun(v: String): Long = v.toLong() * 1000 * 60 * 60 * 24,
    'w' to fun(v: String): Long = v.toLong() * 1000 * 60 * 60 * 24 * 7
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