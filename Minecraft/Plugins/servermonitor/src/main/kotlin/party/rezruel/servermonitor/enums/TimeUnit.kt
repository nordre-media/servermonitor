package party.rezruel.servermonitor.enums

enum class TimeUnit(val value: Long) {
    MILLIS(1),
    SECONDS(1000),
    MINUTES(1000 * 60),
    HOURS(1000 * 60 * 60),
    DAYS(1000 * 60 * 60 * 24),
    WEEKS(1000 * 60 * 60 * 24 * 7)
}