package com.github.ringoame196_s_mcPlugin.commands

import com.github.ringoame196_s_mcPlugin.ItemBagManager
import com.github.ringoame196_s_mcPlugin.PasswordManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Command(plugin: Plugin) : CommandExecutor, TabCompleter {
    private val itemBagManager = ItemBagManager(plugin)
    private val passWordManager = PasswordManager(plugin)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            val message = "${ChatColor.RED}Can Only Players"
            sender.sendMessage(message)
            return true
        }

        val subCommand = args[0]
        when (subCommand) {
            CommandConst.GIVE_COMMAND -> giveCommand(sender, args)
            CommandConst.OPEN_COMMAND -> {
                val id = getBagID(sender)
                if (id != null) itemBagManager.openInv(sender, id)
            }
        }
        return true
    }

    private fun giveCommand(player: Player, args: Array<out String>) {
        val selectPlayers = if (args.size > 1) Bukkit.selectEntities(player, args[1]).filterIsInstance<Player>().toMutableList()
        else mutableListOf(player)

        if (selectPlayers.isEmpty()) {
            val message = "${ChatColor.RED}エンティティが見つかりませんでした"
            player.sendMessage(message)
        } else {
            for (selectPlayer in selectPlayers) {
                val message = "${ChatColor.AQUA}${selectPlayer.displayName}にバッグを与えました"
                itemBagManager.give(selectPlayer)
                player.sendMessage(message)
            }
        }
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
            2 -> {
                when (args[0]) {
                    // パスワード記入
                    CommandConst.OPEN_COMMAND, CommandConst.PASS_COMMAND, CommandConst.RELEASE_COMMAND -> {
                        processPasswordInput(sender, args)
                        return mutableListOf("[パスワード]")
                    }
                    // プレイヤー指定
                    CommandConst.GIVE_COMMAND -> return (Bukkit.getOnlinePlayers().map { it.name } + "@a" + "@p" + "@r" + "@s").toMutableList()
                }
            }
        }
        return mutableListOf()
    }

    private fun processPasswordInput(player: Player, args: Array<out String>) {
        val password = args[1]
        if (password.isEmpty()) {
            // パスワード記入案内
            sendEntryPassWord(player)
        } else if (password[password.length - 1] == '/') {
            // パスワード入力終了
            val subCommand = args[0]
            entryPassWord(player, subCommand, password.replace("/", ""))
        }
    }

    private fun entryPassWord(player: Player, subCommand: String, password: String) {
        val message = "${ChatColor.GOLD}パスワード入力完了\n" +
            "${ChatColor.RED}実行せず閉じてください"
        val sound = Sound.BLOCK_ANVIL_USE
        player.sendMessage(message)
        player.playSound(player, sound, 1f, 1f)

        val id = getBagID(player) ?: return

        when (subCommand) {
            CommandConst.PASS_COMMAND -> {
                if (passWordManager.isLock(id)) {
                    val message = "${ChatColor.RED}既にパスワードがかかっています"
                    player.sendMessage(message)
                } else {
                    val message = "${ChatColor.GOLD}パスワードを設定しました"
                    passWordManager.set(id, password)
                    player.sendMessage(message)
                }
            }

            CommandConst.RELEASE_COMMAND -> {
                if (passWordManager.auth(id, password)) {
                    val message = "${ChatColor.DARK_RED}パスワードを解除しました"
                    passWordManager.delete(id)
                    player.sendMessage(message)
                } else {
                    val message = "${ChatColor.RED}パスワードが違います"
                    player.sendMessage(message)
                }
            }
            CommandConst.OPEN_COMMAND -> itemBagManager.openInv(player, id, password)
        }
    }

    private fun sendEntryPassWord(player: Player) {
        val title = "${ChatColor.GOLD}パスワード記入"
        player.sendTitle(title, "")
        val message = "${ChatColor.RED}[パスワード入力]\n" +
            "${ChatColor.YELLOW}パスワードを入力し、最後に「/」を付けてください、\n" +
            "${ChatColor.RED}そのままEnterは押さないでください"
        player.sendMessage(message)
    }

    private fun getBagID(player: Player): String? {
        val item = player.inventory.itemInMainHand
        val id = itemBagManager.getBagID(item)

        if (id == null) {
            val message = "${ChatColor.RED}バッグをメインハンドに持ちコマンドを実行してください"
            player.sendMessage(message)
            return null
        }
        return id
    }
}
