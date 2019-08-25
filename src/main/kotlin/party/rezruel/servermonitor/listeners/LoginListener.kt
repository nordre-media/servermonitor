package party.rezruel.servermonitor.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import party.rezruel.servermonitor.Monitor


class LoginListener(private val plugin: Monitor) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isOnline) {
            event.player.sendMessage("Hjertelig...Velkommen...Til...Radio...Nordre...MEDIA!!!\n" +
                    "Husk å bli medlem i vår Discord: ${plugin.getLiveDiscordInvite()?.url}"
            )
        }

//        this.plugin.getDiscordChatWebhook().send("**${event.player.name}**: joined the server.")
    }
}