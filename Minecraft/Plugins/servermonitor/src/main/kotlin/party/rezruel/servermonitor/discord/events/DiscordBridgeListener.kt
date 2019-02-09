package party.rezruel.servermonitor.discord.events

import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import party.rezruel.servermonitor.discord.DiscordBridgeObject

class DiscordBridgeListener : ListenerAdapter() {

    private val conf = DiscordBridgeObject
    private val mon = DiscordBridgeObject.monitor

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channel.id != conf.configMap["chat_channel"]) {
            return
        } else {
            mon.server.broadcast(event.message.contentStripped, "monitor.discord.chat")
        }
    }

}