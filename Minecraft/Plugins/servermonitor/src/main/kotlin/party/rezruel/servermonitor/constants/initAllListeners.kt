package party.rezruel.servermonitor.constants

import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.listeners.LoginListener
import party.rezruel.servermonitor.listeners.MinecraftToDiscordEventHandler


fun initAllListeners(plugin: Monitor): Boolean {
    return try {
        val manager = plugin.server.pluginManager

//        manager.registerEvents(BedListener(plugin), plugin)
        manager.registerEvents(LoginListener(plugin), plugin)
        manager.registerEvents(MinecraftToDiscordEventHandler(plugin), plugin)

        true
    } catch (exc: Exception) {
        false
    }
}

