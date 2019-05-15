package party.rezruel.servermonitor.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import party.rezruel.servermonitor.Monitor


class LoginListener(private val plugin: Monitor) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isOnline and (event.player is Player)) {
            event.player.sendMessage("Hjertelig...Velkommen...Til...Radio...Nordre...MEDIA!!!\n" +
                    "Husk å bli medlem i vår Discord: https://discord.gg/R8XXyMB")
        }

//        this.plugin.getDiscordChatWebhook().send("**${event.player.name}**: joined the server.")
    }
}