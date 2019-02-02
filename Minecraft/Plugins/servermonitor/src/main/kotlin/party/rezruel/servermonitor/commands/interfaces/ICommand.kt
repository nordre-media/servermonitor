package party.rezruel.servermonitor.commands.interfaces

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import party.rezruel.servermonitor.Monitor

interface ICommand {
    fun execute(
        sender: CommandSender,
        plugin: Monitor
    ): Boolean { throw NotImplementedError() }

    fun execute(
        sender: CommandSender,
        args: Array<out String>,
        plugin: Monitor
    ): Boolean { throw NotImplementedError() }

    fun execute(
        sender: CommandSender,
        label: String,
        args: Array<out String>,
        plugin: Monitor
    ): Boolean { throw NotImplementedError() }

    fun execute(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
        plugin: Monitor
    ): Boolean { throw NotImplementedError() }
}
