package party.rezruel.servermonitor.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.command.CommandSender
import org.jetbrains.annotations.Nullable
import party.rezruel.servermonitor.Monitor

@Suppress("DEPRECATION")
@CommandAlias("monitor|mon")
class MonitorCommand(private val monitor: Monitor, cmd: @Nullable String?) : BaseCommand(cmd) {

    @Subcommand("config")
    @CommandPermission("monitor.config")
    @Description("Gets or sets a config key to a value")
    @Syntax("[get] <key> [value]")
    fun onConfig(sender: CommandSender, args: Array<out String>) {
        ConfigCommand.execute(sender, args, monitor)
    }

    @Subcommand("log")
    @CommandPermission("monitor.log")
    @Description("Sends a log message to Discord")
    fun onLog(sender: CommandSender) {
        LogCommand.execute(sender, monitor)
    }

    @Subcommand("schedulerestart")
    @CommandPermission("monitor.schedulerestart")
    @Description("Schedules a reload")
    @CommandAlias("restart")
    @Syntax("<timeformat>")
    fun onSchedulerestart(sender: CommandSender, args: Array<out String>) {
        ScheduleRestartCommand.execute(sender, args, monitor)
    }

}