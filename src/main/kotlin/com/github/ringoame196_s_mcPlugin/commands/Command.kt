package com.github.ringoame196_s_mcPlugin.commands

import org.bukkit.ChatColor
import org.bukkit.Sound
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
        val sender = commandSender as? Player ?: return mutableListOf()
        when (args.size) {
            // tabにサブコマンドを表示
            1 -> return mutableListOf(
                CommandConst.GIVE_COMMAND,
                CommandConst.OPEN_COMMAND,
                CommandConst.PASS_COMMAND,
                CommandConst.RELEASE_COMMAND
            )
            // パスワード記入
            2 -> {
                when (args[0]) {
                    CommandConst.OPEN_COMMAND, CommandConst.PASS_COMMAND, CommandConst.RELEASE_COMMAND -> {
                        processPasswordInput(sender, args)
                        return mutableListOf("[パスワード]")
                    }
                }
            }
        }
        return mutableListOf()
    }

    private fun processPasswordInput(player: Player, args: Array<out String>) {
        val subCommand = args[0]
        val password = args[1]
        if (password.isEmpty()) {
            // パスワード記入案内
            sendEntryPassWord(player)
        } else if (password[password.length - 1] == '/') {
            // パスワード入力終了
            entryPassWord(player, subCommand, password.replace("/", ""))
        }
    }

    private fun entryPassWord(player: Player, subCommand: String, password: String) {
        val message = "${ChatColor.GOLD}パスワード入力完了\n" +
            "${ChatColor.RED}実行せず閉じてください"
        val sound = Sound.BLOCK_ANVIL_USE
        player.sendMessage(message)
        player.playSound(player, sound, 1f, 1f)
    }

    private fun sendEntryPassWord(player: Player) {
        val title = "${ChatColor.GOLD}パスワード記入"
        player.sendTitle(title, "")
        val message = "${ChatColor.RED}[パスワード入力]\n" +
            "${ChatColor.YELLOW}パスワードを入力し、最後に「/」を付けてください、\n" +
            "${ChatColor.RED}そのままEnterは押さないでください"
        player.sendMessage(message)
    }
}
