package party.rezruel.servermonitor.discord

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.MessageBuilder
import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.discord.events.DiscordBridgeListener
import party.rezruel.servermonitor.enums.TimeUnit
import java.time.Instant
import java.time.ZoneOffset
import kotlin.concurrent.thread

class DiscordClient(private val monitor: Monitor) {

    val jda = this.buildJDA()

    private var statusThread: Thread

    init {

        DiscordObjectOrientedStateSucks.monitor = monitor

        this.jda.addEventListener(DiscordBridgeListener())

        this.statusThread = this.updateStatusInfoMessage()

    }

    private fun buildJDA(): JDA {
//        monitor.logger.info("Discord token: ${this.token()}")
        val client = JDABuilder(token()).build()
        client.awaitReady()
        return client
    }

    private fun token() = monitor.config.getString("bot_token")
    private fun channel() = monitor.config.getString("status_channel")
    private fun message() = monitor.config.getString("status_message")
    private fun interval() = monitor.config.getString("status_interval").toLong()
    private fun timeUnit() = TimeUnit.valueOf(monitor.config.getString("time_unit_status").toUpperCase()).value
    private fun discordChannel() = jda.getTextChannelById(channel())
    private fun discordMessage() = discordChannel().getMessageById(message()).complete()

    private fun embedContent() = MessageBuilder().setContent("\u200b").setEmbed(monitor.publicStatsToEmbed()).build()

    private fun updateStatusInfoMessage(): Thread {
        return thread(start = true, isDaemon = true) {
            while (true) {
//                monitor.logger.info("msg id: ${discordMessage().id} chan id: ${discordMessage().channel.id}")
                try {
                    discordMessage().editMessage(embedContent()).queue()
                } catch (exc: Exception) {
                    monitor.logger.info("$exc\n${exc.cause}")
                    exc.printStackTrace()
                    discordMessage().editMessage("En error occurred while updating the status message." +
                            "\nEdited: ${Instant.now().atOffset(ZoneOffset.UTC)}").queue()
                }
//                monitor.logger.info("Sleeping status message thread")
                Thread.sleep(timeUnit() * interval())
            }
        }
    }

    private fun cancelStatusInfoMessage(): Boolean {
        return if (this.statusThread.isInterrupted) {
            false
        } else {
            this.statusThread.interrupt()
            true
        }
    }

    fun restartStatusInfoMessage(): Boolean {
        return if (this.cancelStatusInfoMessage()) {
            this.updateStatusInfoMessage()
            true
        } else false
    }

    fun cleanShutdown(): Boolean {
        return try {
            this.cancelStatusInfoMessage()
            this.jda.shutdown()
            true
        } catch (exc: Exception) {
            false
        }
    }
}