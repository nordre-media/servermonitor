package party.rezruel.servermonitor.commands.constants

import party.rezruel.servermonitor.commands.ConfigCommand
import party.rezruel.servermonitor.commands.LogCommand
import party.rezruel.servermonitor.commands.ScheduleRestartCommand

val allCommands = mapOf(
    "log" to LogCommand,
    "config" to ConfigCommand,
    "schedulerestart" to ScheduleRestartCommand
)