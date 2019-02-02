package party.rezruel.servermonitor.listeners

import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import party.rezruel.servermonitor.Monitor

class BedListener(private val plugin: Monitor) : Listener {

    private val inBedList = mutableMapOf<World, MutableList<Player>>()

    init {
        for (world in plugin.server.worlds) {
            inBedList[world] = mutableListOf()
        }
    }

    @EventHandler
    fun onPlayerBedEnter(event: PlayerBedEnterEvent) {
        inBedList[event.player.world]?.add(event.player)
        dayNightCycleMaybe(event.player.world)
    }

    @EventHandler
    fun onPlayerBedLeave(event: PlayerBedLeaveEvent) {
        inBedList[event.player.world]?.remove(event.player)
        dayNightCycleMaybe(event.player.world)
    }

    private fun dayNightCycleMaybe(world: World) {
        if (inBedList[world]!!.size >= (plugin.server.getWorld(world.uid)!!.players!!.size / 2)) {
            world.fullTime = 0
        }
    }
}