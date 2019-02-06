package party.rezruel.servermonitor.listeners

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.enums.TimeUnit
import java.time.Instant


class MinecraftToDiscordEventHandler(private val plugin: Monitor) : Listener {


    private var chatList = mutableListOf<String>()
    private var lastSent: Instant = Instant.now()

    private fun cacheDelay(): Long {
        return this.plugin.config["cache_delay"].toString().toLong()
    }

    private fun chatCacheLimit(): Int {
        return this.plugin.config["chat_cache"].toString().toInt()
    }

    private fun chatCacheTimeUnit() = this.plugin.config["time_unit_cache"].toString().toUpperCase()

    private val coroutine = GlobalScope.launch {
        while (this@MinecraftToDiscordEventHandler.plugin.isEnabled) {
            this@MinecraftToDiscordEventHandler.sendToDiscord()
            delay(500)
        }
    }

//    private val thread = kotlin.concurrent.thread(false, true) {
//        while (this.plugin.isEnabled) {
//            this.sendToDiscord()
//            Thread.sleep(500)
//        }
//    }

    init {
        this.lastSent = Instant.now()
//        this.thread.start()
        this.coroutine.start()
    }

    @EventHandler
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        if (event.player.isOnline) {
            chatList.add("${event.player.name}: ${event.message}")
        }
        this.sendToDiscord()
    }

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        chatList.add("${event.player.name}: Joined the server.")
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        chatList.add("${event.player.name}: Left the server.")
    }

    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        if (this.plugin.name == event.plugin.name) {
            this.coroutine.cancel()
        }
    }

    private fun sendToDiscord() {
        if (this.chatList.size == this.chatCacheLimit()
                ||
                ((Instant.now().toEpochMilli() >= this.lastSent.plusMillis((this.cacheDelay() * TimeUnit.valueOf(chatCacheTimeUnit()).value)).toEpochMilli())
                        &&
                        (this.chatList.isNotEmpty()))
        ) {
            this.plugin.getDiscordChatWebhook().send(this.chatList.joinToString(separator = "\n"))
            this.lastSent = Instant.now()
            this.chatList = mutableListOf()
        }
    }
}