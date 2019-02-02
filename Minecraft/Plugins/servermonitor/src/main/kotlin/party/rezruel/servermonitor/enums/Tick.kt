package party.rezruel.servermonitor.enums

enum class Tick(val value: Long) {
    SECOND(20),
    MINUTE(20*60),
    HOUR(20*60*60)
}