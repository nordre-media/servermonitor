package party.rezruel.servermonitor.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.commands.interfaces.ICommand
import party.rezruel.servermonitor.helpers.parseDuration
import java.time.Instant
import java.time.ZoneId
import kotlin.concurrent.thread

object ScheduleRestartCommand : ICommand {
    private var done: Boolean = false
    private var currentlyRunningThread: Thread = thread {}

    @JvmStatic
    private fun makeThread(
        sender: CommandSender,
        args: Array<out String>,
        plugin: Monitor
    ): Thread {
        return thread(true, true) {
            this@ScheduleRestartCommand.done = false
            val duration = parseDuration(args[0])
            val atTime = Instant.ofEpochMilli(
                Instant.now().toEpochMilli() + duration
            ).atZone(ZoneId.of("CET")).toLocalDateTime()

            val shutdownString = "Server has been scheduled to restart at: " +
                    "${atTime.year}-${atTime.month}-${atTime.dayOfMonth}-" +
                    "${atTime.hour}:${atTime.minute}:${atTime.second}" +
                    "\nby ${sender.name}"

            plugin.server.broadcastMessage(shutdownString)
            plugin.getDiscordWebhook().send(shutdownString)
            plugin.getDiscordChatWebhook().send(shutdownString)

            Thread.sleep(duration)
            this@ScheduleRestartCommand.done = true
            plugin.server.shutdown()
        }
    }

    override fun execute(
        sender: CommandSender,
        args: Array<out String>,
        plugin: Monitor
    ): Boolean {
        if (args[0].isBlank()) return false

        return if (currentlyRunningThread.isInterrupted) {
            makeThread(sender, args, plugin)
            true
        } else if (!done) {
            try {
                currentlyRunningThread.interrupt()
            } catch (exc: InterruptedException) {
                sender.sendMessage("Cancelled previously scheduled shutdown.")
            }
            currentlyRunningThread = makeThread(sender, args, plugin)
            true
        } else {
            false
        }

    }
}