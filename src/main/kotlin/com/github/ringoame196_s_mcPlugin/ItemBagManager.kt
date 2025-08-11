package com.github.ringoame196_s_mcPlugin

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.UUID

class ItemBagManager(plugin: Plugin) {
    private val passwordManager = PasswordManager(plugin)
    private val material = Material.BARREL
    private val name = "${ChatColor.GOLD}アイテムバッグ"
    private val idKey = NamespacedKey(plugin, "bag_id")
    private val guiName = "${ChatColor.BLUE}アイテムバッグ"
    private val guiSize = 27

    fun give(player: Player) {
        val bagItem = makeItem()
        player.inventory.addItem(bagItem)
    }

    private fun makeItem(): ItemStack {
        val id = UUID.randomUUID().toString()
        val item = ItemStack(material)
        val meta = item.itemMeta ?: return item
        meta.setDisplayName(name)
        meta.persistentDataContainer.set(idKey, PersistentDataType.STRING, id)
        item.itemMeta = meta
        return item
    }

    fun getBagID(item: ItemStack): String? {
        val meta = item.itemMeta ?: return null
        return meta.persistentDataContainer.get(idKey, PersistentDataType.STRING)
    }

    private fun getInventory(id: String): Inventory {
        val cacheInventory = Data.bagCashInventory[id]
        if (cacheInventory != null) {
            return cacheInventory
        } else {
            val inventory = Bukkit.createInventory(null, guiSize, guiName)
            Data.bagCashInventory[id] = inventory
            return inventory
        }
    }

    fun openInv(player: Player, id: String, password: String? = null) {
        if (!passwordManager.auth(id, password)) {
            val message = "${ChatColor.RED}ロックがかかっています"
            val sound = Sound.BLOCK_CHEST_LOCKED
            player.sendMessage(message)
            player.playSound(player, sound, 1f, 1f)
            return
        }
        val inventory = getInventory(id)
        val sound = Sound.BLOCK_BARREL_OPEN
        player.openInventory(inventory)
        player.playSound(player, sound, 1f, 1f)
        Data.openBagInventory[player.uniqueId] = id
    }

    fun closeInv(player: Player) {
        val uuid = player.uniqueId
        val id = Data.openBagInventory[uuid]
        val sound = Sound.BLOCK_BARREL_CLOSE
        player.playSound(player, sound, 1f, 1f)
        Data.openBagInventory.remove(uuid)
    }

    fun isBagInventory(inventoryTitle: String): Boolean {
        return inventoryTitle == guiName
    }
}
