package party.rezruel.servermonitor.discord

import kotlinx.coroutines.*
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.MessageBuilder
import party.rezruel.servermonitor.Monitor
import party.rezruel.servermonitor.discord.events.DiscordBridgeListener
import party.rezruel.servermonitor.enums.TimeUnit

@ExperimentalCoroutinesApi
class DiscordClient(private val monitor: Monitor) {

    @ExperimentalCoroutinesApi
    val JDA by lazy {
        this.asyncJDA.getCompleted()
    }

    private val asyncCoro by lazy {
        GlobalScope.async {
            JDABuilder(monitor.config.getString(token())).addEventListener(DiscordBridgeListener())
        }
    }

    private val asyncJDA by lazy {
        GlobalScope.async {
            asyncCoro.await().build()
        }
    }

    private val discordConfigMap = mutableMapOf<String, String>()

    private fun token() = monitor.config.getString("bot_token")

    init {
        this.asyncCoro
        this.asyncJDA

        runBlocking {
            launch {
                val conf = this@DiscordClient.monitor.config
                val map = this@DiscordClient.discordConfigMap

                map["modlog_channel"] = conf.get("modlog_channel").toString()
                map["status_channel"] = conf.get("status_channel").toString()
                map["status_message"] = conf.get("status_message").toString()
                map["chat_channel"] = conf.get("chat_channel").toString()

                DiscordBridgeObject.configMap = map
                DiscordBridgeObject.monitor = monitor
            }
        }
    }

    private suspend fun updateStatusInfoMessage() {
        while (monitor.isEnabled) {
            if (JDA.status != net.dv8tion.jda.core.JDA.Status.CONNECTED) {
                continue
            }
            delay(
                    TimeUnit.valueOf(monitor.config.get("time_unit_status").toString().toUpperCase()).value
                            * monitor.config.get("status_interval").toString().toLong()
            )
            JDA.getTextChannelById(monitor.config.get("status_message").toString())
                    .getMessageById(monitor.config.get("status_message").toString())
                    .complete()
                    .editMessage(MessageBuilder().setEmbed(monitor.statsToEmbed()).build())
                    .queue()
        }
    }
}