package com.github.ringoame196_s_mcPlugin.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Command : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            val message = "${ChatColor.RED}Can Only Players"
            sender.sendMessage(message)
            return true
        }
        return true
    }

    override fun onTabComplete(commandSender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        when(args.size) {
            1 -> return mutableListOf(
                CommandConst.GIVE_COMMAND,
                CommandConst.OPEN_COMMAND,
                CommandConst.PASS_COMMAND,
                CommandConst.RELEASE_COMMAND
            )
        }
        return mutableListOf()
    }
}
