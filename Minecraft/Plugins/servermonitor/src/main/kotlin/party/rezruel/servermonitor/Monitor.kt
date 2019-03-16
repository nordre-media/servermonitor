package party.rezruel.servermonitor

import co.aikar.commands.PaperCommandManager
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.webhook.WebhookClient
import net.dv8tion.jda.webhook.WebhookClientBuilder
import net.dv8tion.jda.webhook.WebhookMessageBuilder
import org.bukkit.plugin.java.JavaPlugin
import party.rezruel.servermonitor.commands.MonitorCommand
import party.rezruel.servermonitor.constants.initAllListeners
import party.rezruel.servermonitor.discord.DiscordClient
import party.rezruel.servermonitor.enums.*
import party.rezruel.servermonitor.helpers.getPlayerStatsMap
import java.lang.management.ManagementFactory
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import kotlin.concurrent.thread


class Monitor : JavaPlugin() {

    companion object {
        @JvmStatic
        fun main() {
            println("This is not a standalone jar")
        }
    }

    private val statsCoroutine = thread(false, true) {
        Thread.sleep(1000)
        while (this@Monitor.isEnabled) {
            this@Monitor.server.consoleSender.sendMessage("Logging to Discord...")
            this@Monitor.webhook.send(listOf(modStatsToEmbed()))
            this@Monitor.server.consoleSender.sendMessage("Logged to Discord.")
            Thread.sleep(
                    TimeUnit.valueOf(this@Monitor.config.getString("time_unit_log").toUpperCase()).value
                            * this@Monitor.config.getString("log_interval").toLong()
            )
        }
    }

    private val webhook by lazy {
        try {
            WebhookClientBuilder(this.config.getString("webhook")).build()
        } catch (exception: IllegalArgumentException) {
            throw RuntimeException(
                    "No configured webhook url for ${this.name}. " +
                            "Value is ${config.get("webhook")} " +
                            "Please configure one (Copy and paste webhook url from Discord) " +
                            "into the config.yml like: " +
                            "webhook: \"https://discordapp.com/api/webhooks/000000000000000000" +
                            "/aduhgawduih_adygd2ugy1dib_cg8g12d679gagsdybvd87\"\n" +
                            "${exception.cause}\n${exception.message}\n${exception.printStackTrace()}"
            )

        }
    }

    private val chatWebhook by lazy {
        try {
            WebhookClientBuilder(this.config.getString("discord_chat_webhook")).build()
        } catch (exception: IllegalArgumentException) {
            throw RuntimeException(
                    "No configured chat webhook url for ${this.name}. " +
                            "Value is ${config.get("discord_chat_webhook")} " +
                            "Please configure one (Copy and paste webhook url from Discord) " +
                            "into the config.yaml like:" +
                            "discord_chat_webhook: \"https://discordapp.com/api/webhooks/000000000000000000" +
                            "/aduhgawduih_adygd2ugy1dib_cg8g12d679gagsdybvd87\"\n" +
                            "${exception.cause}\n${exception.message}\n${exception.printStackTrace()}"
            )
        }
    }

    private val discordClient = DiscordClient(this)

    fun getDiscordWebhook(): WebhookClient = this.webhook

    fun getDiscordChatWebhook(): WebhookClient = this.chatWebhook

    fun sendStatsToDiscord(author: String? = null) {
        this.webhook.send(
                WebhookMessageBuilder()
                        .addEmbeds(
                                listOf(this.modStatsToEmbed())
                        )
                        .setContent(
                                if (author != null) "Stats sent early by: $author" else "Stats sent early by an unknown author"
                        ).build()
        )
    }

    private fun serverStatsMap(): Map<ServerStatsEnum, Any> {

        val server = this.server
        val onlinePlayers = server.onlinePlayers.size
        val offlinePlayers = server.offlinePlayers.size
        val tpsLastMinute = server.tps[0]
        val serverName = server.serverName ?: "Server"
        val bannedPlayerCount = server.bannedPlayers.size
        val maxPlayers = server.maxPlayers
        val operators = server.operators.size
        val operatorNameAndIds = mutableListOf<String>()
        val serverVersion = server.version
        val bukkitServerVersion = server.bukkitVersion

        server.operators.forEach { operatorNameAndIds.add("${it.name}(${it.uniqueId})\n") }

        return mapOf(
                ServerStatsEnum.ONLINE_PLAYERS to onlinePlayers,
                ServerStatsEnum.OFFLINE_PLAYERS to offlinePlayers,
                ServerStatsEnum.TPS to tpsLastMinute,
                ServerStatsEnum.SERVER_NAME to serverName,
                ServerStatsEnum.BAN_COUNT to bannedPlayerCount,
                ServerStatsEnum.MAX_PLAYERS to maxPlayers,
                ServerStatsEnum.OPERATOR_COUNT to operators,
                ServerStatsEnum.OPERATORS to operatorNameAndIds,
                ServerStatsEnum.SERVER_VERSION to serverVersion,
                ServerStatsEnum.BUKKIT_SERVER_VERSION to bukkitServerVersion
        )
    }

    private fun runtimeStatsMap(): Map<RuntimeStatsEnum, Long> {

        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        val freeMemory = runtime.freeMemory() / 1024 / 1024
        val totalMemory = runtime.totalMemory() / 1024 / 1024
        val usedMemory = totalMemory - freeMemory

        return mapOf(
                RuntimeStatsEnum.MAX_MEMORY to maxMemory,
                RuntimeStatsEnum.FREE_MEMORY to freeMemory,
                RuntimeStatsEnum.TOTAL_MEMORY to totalMemory,
                RuntimeStatsEnum.USED_MEMORY to usedMemory
        )
    }

    private fun osStatsMap(): Map<OsStatsEnum, Any> {

//        val osBean = (ManagementFactory.getPlatformMXBeans(OperatingSystemMXBean::class.java)
//                as com.sun.management.OperatingSystemMXBean)
        val osBean = ManagementFactory.getOperatingSystemMXBean()
        val availableProcessors = ManagementFactory.getOperatingSystemMXBean().availableProcessors
        val upTimeMillis = ManagementFactory.getRuntimeMXBean().uptime
        val upTime = ManagementFactory.getRuntimeMXBean().uptime / 1000
//        val processCpuLoad = osBean.processCpuLoad ?: 0.0
//        val systemCpuLoad = osBean.systemCpuLoad ?: 0.0
        val systemCpuLoadAverage = osBean.systemLoadAverage

        val upTimeSince = Instant.now().minusMillis(upTimeMillis).atZone(ZoneId.of("CET"))

        return mapOf(
                OsStatsEnum.AVAILABLE_PROCESSORS to availableProcessors,
                OsStatsEnum.UPTIME to upTime,
                OsStatsEnum.UPTIME_SINCE to upTimeSince,
                OsStatsEnum.SYSTEM_CPU_AVERAGE_LOAD to systemCpuLoadAverage
//            OsStatsEnum.PROCESS_CPU_LOAD to processCpuLoad,
//            OsStatsEnum.SYSTEM_CPU_LOAD to systemCpuLoad
        )
    }

    private fun threadStatsMap(): Map<ThreadStatsEnum, Any> {

        val threadBean = ManagementFactory.getThreadMXBean()
        val daemonThreadCount = threadBean.daemonThreadCount
        val liveThreadCount = threadBean.threadCount
        val realLiveThreadCount = liveThreadCount - daemonThreadCount
        val totalStartedThreadCount = threadBean.totalStartedThreadCount
        val peakThreadCount = threadBean.peakThreadCount

        return mapOf(
                ThreadStatsEnum.DAEMON_THREAD_COUNT to daemonThreadCount,
                ThreadStatsEnum.LIVE_THREAD_COUNT to liveThreadCount,
                ThreadStatsEnum.REAL_LIVE_THREAD_COUNT to realLiveThreadCount,
                ThreadStatsEnum.TOTAL_STARTED_THREAD_COUNT to totalStartedThreadCount,
                ThreadStatsEnum.PEAK_THREAD_COUNT to peakThreadCount
        )
    }

    fun modStatsToEmbed(inline: Boolean = true): MessageEmbed {
        val serverStats = this.serverStatsMap()
        val runtimeStats = this.runtimeStatsMap()
        val osStats = this.osStatsMap()
        val threadStats = this.threadStatsMap()
        val playerStats = getPlayerStatsMap(this)

        try {
            return EmbedBuilder().addField(
                    "Server stats",
                    "Server name: ${serverStats[ServerStatsEnum.SERVER_NAME]}\n" +
                            "Online: ${serverStats[ServerStatsEnum.ONLINE_PLAYERS]}\n" +
                            "Offline: ${serverStats[ServerStatsEnum.OFFLINE_PLAYERS]}\n" +
                            "Max: ${serverStats[ServerStatsEnum.MAX_PLAYERS]}\n" +
                            "Operator count: ${serverStats[ServerStatsEnum.OPERATOR_COUNT]}\n" +
                            "Operators: ${serverStats[ServerStatsEnum.OPERATORS]}\n" +
                            "Bans: ${serverStats[ServerStatsEnum.BAN_COUNT]}\n" +
                            "TPS: ${serverStats[ServerStatsEnum.TPS]}\n" +
                            "VERSION: ${serverStats[ServerStatsEnum.SERVER_VERSION]}\n" +
                            "BUKKIT_VERSION: ${serverStats[ServerStatsEnum.BUKKIT_SERVER_VERSION]}",
                    inline
            ).addField(
                    "Runtime stats",
                    "Max memory: ${runtimeStats[RuntimeStatsEnum.MAX_MEMORY]} MB\n" +
                            "Free memory: ${runtimeStats[RuntimeStatsEnum.FREE_MEMORY]} MB\n" +
                            "Total memory: ${runtimeStats[RuntimeStatsEnum.TOTAL_MEMORY]} MB\n" +
                            "Used memory: ${runtimeStats[RuntimeStatsEnum.USED_MEMORY]} MB",
                    inline
            ).addField(
                    "Os stats",
                    "Uptime since: ${osStats[OsStatsEnum.UPTIME_SINCE]}\n" +
                            "Available logical cores: ${osStats[OsStatsEnum.AVAILABLE_PROCESSORS]}\n" +
                            "Average system CPU load ${osStats[OsStatsEnum.SYSTEM_CPU_AVERAGE_LOAD]}",
//                        "Process CPU load: ${osStats[OsStatsEnum.PROCESS_CPU_LOAD]}\n" +
//                        "System CPU load: ${osStats[OsStatsEnum.SYSTEM_CPU_LOAD]}",
                    inline
            ).addField(
                    "Thread stats",
                    "Live threads: ${threadStats[ThreadStatsEnum.LIVE_THREAD_COUNT]}\n" +
                            "Daemon threads: ${threadStats[ThreadStatsEnum.DAEMON_THREAD_COUNT]}\n" +
                            "Real threads: ${threadStats[ThreadStatsEnum.REAL_LIVE_THREAD_COUNT]}\n" +
                            "Peak threads: ${threadStats[ThreadStatsEnum.PEAK_THREAD_COUNT]}\n" +
                            "Lifetime threads: ${threadStats[ThreadStatsEnum.TOTAL_STARTED_THREAD_COUNT]}",
                    inline
            ).addField(
                    "Player stats",
                    "Online players: " +
                            (playerStats[PlayerStatsEnum.ALL_ONLINE_USERS_BY_NAME]?.joinToString(", ") ?: "None"),
                    inline
            ).setColor(0x00AFDF).setTimestamp(
                    Instant.now().atZone(ZoneOffset.UTC)
            ).setAuthor("Brought to you by Rezruel#4080").build()
        } catch (exc: IllegalStateException) {
            throw exc
        }
    }

    fun publicStatsToEmbed(inline: Boolean = false): MessageEmbed {
        val serverStats = this.serverStatsMap()
        val osStats = this.osStatsMap()
        val playerStats = getPlayerStatsMap(this)

        try {
            return EmbedBuilder().addField(
                    "Server stats",
                    "Server name: ${serverStats[ServerStatsEnum.SERVER_NAME]}\n" +
                            "Online: ${serverStats[ServerStatsEnum.ONLINE_PLAYERS]}\n" +
                            "Offline: ${serverStats[ServerStatsEnum.OFFLINE_PLAYERS]}\n" +
                            "Max: ${serverStats[ServerStatsEnum.MAX_PLAYERS]}\n" +
                            "Operator count: ${serverStats[ServerStatsEnum.OPERATOR_COUNT]}\n" +
                            "TPS: ${serverStats[ServerStatsEnum.TPS]}\n" +
                            "VERSION: ${serverStats[ServerStatsEnum.SERVER_VERSION]}\n" +
                            "BUKKIT_VERSION: ${serverStats[ServerStatsEnum.BUKKIT_SERVER_VERSION]}",
                    inline
            ).addField(
                    "Os stats",
                    "Uptime since: ${osStats[OsStatsEnum.UPTIME_SINCE]}\n",
                    inline
            ).addField(
                    "Player stats",
                    "Online players: " +
                            (playerStats[PlayerStatsEnum.ALL_ONLINE_USERS_BY_NAME]?.joinToString(", ") ?: "None"),
                    inline
            ).setColor(0x00AFDF).setTimestamp(
                    Instant.now().atZone(ZoneOffset.UTC)
            ).setAuthor("Brought to you by Rezruel#4080").build()
        } catch (exc: IllegalStateException) {
            throw exc
        }
    }

    private fun loadCommands() {
        val commandManager = PaperCommandManager(this)
        commandManager.enableUnstableAPI("help")

        commandManager.commandCompletions.registerCompletion("subcommand") { c ->
            val lower = c.input.toLowerCase()
            val commands = LinkedHashSet<String>()
            val subcommands = commandManager.getRootCommand("monitor").subCommands
            for (kvp in subcommands.entries()) {
                if (!kvp.value.isPrivate() && (lower.isEmpty() || kvp.key.toLowerCase().startsWith(lower)) && kvp.value.getCommand().indexOf(
                                ' '
                        ) == -1
                ) {
                    commands.add(kvp.value.getCommand())
                }
            }
            commands.toMutableList()
        }

        commandManager.registerCommand(MonitorCommand(this, "monitor"))
    }

    override fun onEnable() {
//        val redisStats = RedisStats
//        redisStats.setMonitor(this)
        this.reloadConfig()
        this.saveConfig()

        // Why are we unloading while also throwing? To be dumb of course.
        if (!initAllListeners(this)) {
            throw RuntimeException("Could not initialise all listeners. Disabling..." +
                    "${this.pluginLoader.disablePlugin(this)}")
        }

//        for ((key, _) in allCommands) {
//            this.getCommand(key).executor = MonitorCommandExecutor(this)
//        }
        this.statsCoroutine.start()
        this.webhook.send("${this.name} enabled.")

        this.loadCommands()
    }


    override fun onDisable() {
        this.reloadConfig()
        this.saveConfig()
        this.webhook.send("${this.name} disabled.")
        this.statsCoroutine.interrupt()
        this.server.scheduler.cancelTasks(this)

        this.discordClient.cleanShutdown()
    }

    override fun onLoad() {
        this.webhook.send("${this.name} loaded.")
        this.discordClient.jda.awaitReady()
    }
}