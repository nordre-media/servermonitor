package party.rezruel.servermonitor.commands.executors

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.commands.constants.allCommands

class MonitorCommandExecutor(private val plugin: Monitor): CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val commandNameLowercase = command.name.toLowerCase()

        return if (commandNameLowercase in allCommands) {
            allCommands.getValue(commandNameLowercase).execute(sender, command, label, args, this.plugin)
        } else {
            false
        }
    }

}