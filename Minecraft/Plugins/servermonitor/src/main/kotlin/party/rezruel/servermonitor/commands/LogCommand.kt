package party.rezruel.servermonitor.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.commands.interfaces.ICommand
import java.util.logging.Level

object LogCommand : ICommand {
    override fun execute(
        sender: CommandSender,
        plugin: Monitor
    ): Boolean {
        return try {
            plugin.sendStatsToDiscord(if (sender is Player) sender.name else null)
            true
        } catch (exception: IllegalArgumentException) {
            plugin.logger.log(Level.WARNING, "${exception.message}\n${exception.cause}\n${exception.printStackTrace()}")
            false
        }
    }
}