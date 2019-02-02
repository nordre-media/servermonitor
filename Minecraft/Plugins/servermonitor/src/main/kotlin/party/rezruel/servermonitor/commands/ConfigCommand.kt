package party.rezruel.servermonitor.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.commands.interfaces.ICommand
import java.util.logging.Level

object ConfigCommand : ICommand {
    override fun execute(
        sender: CommandSender,
        args: Array<out String>,
        plugin: Monitor
    ): Boolean {
        return try {
            when {
                args[0].toLowerCase() == "set" -> {
                    if (args[1].isBlank() or args[2].isBlank()) {
                        return false
                    }
                    plugin.config.set(args[1], args[2])
                    sender.sendMessage("Set ${args[1]} to ${args[2]}")
                    plugin.saveConfig()
                    plugin.reloadConfig()
                }
                plugin.config.get(args[0]) != null -> sender.sendMessage(
                    "${args[0]} is currently set to ${plugin.config.get(
                        args[0]
                    )}"
                )
                else -> return false
            }
            true
        } catch (exception: RuntimeException) {
            plugin.logger.log(Level.WARNING, "${exception.message}\n${exception.cause}\n${exception.printStackTrace()}")
            false
        }
    }
}