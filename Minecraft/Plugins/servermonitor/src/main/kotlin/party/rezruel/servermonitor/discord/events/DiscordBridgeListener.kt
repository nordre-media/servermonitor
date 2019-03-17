package party.rezruel.servermonitor.discord.events

import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import party.rezruel.servermonitor.discord.DiscordObjectOrientedStateSucks

class DiscordBridgeListener : ListenerAdapter() {

    val monitor = DiscordObjectOrientedStateSucks.monitor

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channel.id != monitor.config.getString("chat_channel") || event.author.isBot) {
            return
        } else {
            monitor.server.broadcast(
                    "§b${event.author.name}§r#§6${event.author.discriminator}§r: ${event.message.contentStripped}",
                    "monitor.discord.chat"
            )
        }
    }

}