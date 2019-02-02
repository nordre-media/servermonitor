package party.rezruel.servermonitor.helpers

import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.enums.PlayerStatsEnum

fun getPlayerStatsMap(plugin: Monitor): Map<PlayerStatsEnum, MutableList<String>> {

    val onlinePlayersByName = mutableListOf<String>()
    val onlinePlayersByUUID = mutableListOf<String>()
    val offlinePlayersByName = mutableListOf<String>()
    val offlinePlayersByUUID = mutableListOf<String>()

    plugin.server.onlinePlayers.forEach {
        onlinePlayersByName.add(it.name)
        onlinePlayersByUUID.add(it.uniqueId.toString())
    }

    plugin.server.offlinePlayers.forEach {
        if (!it.isOnline) {
            offlinePlayersByName.add(it.name)
            offlinePlayersByUUID.add(it.uniqueId.toString())
        }
    }

    return mapOf(
        PlayerStatsEnum.ALL_ONLINE_USERS_BY_NAME to onlinePlayersByName,
        PlayerStatsEnum.ALL_ONLINE_USERS_BY_UUID to onlinePlayersByUUID,
        PlayerStatsEnum.ALL_OFFLINE_USERS_BY_NAME to offlinePlayersByName,
        PlayerStatsEnum.ALL_OFFLINE_USERS_BY_UUID to offlinePlayersByUUID
    )
}