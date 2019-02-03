package party.rezruel.servermonitor.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
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
    @Syntax("<key> [get:set] [value]")
    fun onConfig(
            sender: CommandSender,
            key: String,
            @Default("get") setOrGet: String,
            @Optional value: String) {
        ConfigCommand.execute(sender, setOrGet, key, value, monitor)
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
    fun onSchedulerestart(sender: CommandSender, @Default("1h") timeformat: String) {
        ScheduleRestartCommand.execute(sender, timeformat, monitor)
    }

    @CatchUnknown
    @Default
    @CommandCompletion("@subcommand")
    fun onDefault(sender: CommandSender, args: Array<out String>) {
        monitor.server.dispatchCommand(sender, "monitor help")
    }

    @HelpCommand
    @Syntax("[command]")
    fun onHelp(sender: CommandSender, help: CommandHelp) {
        help.showHelp()
    }

}