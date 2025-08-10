package com.github.ringoame196_s_mcPlugin

import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.UUID

class ItemBagManager(plugin: Plugin) {
    private val material = Material.BARREL
    private val name = "${ChatColor.GOLD}アイテムバッグ"
    private val idKey = NamespacedKey(plugin, "bag_id")

    fun give(player: Player) {
        val bagItem = makeItem()
        player.inventory.addItem(bagItem)
    }

    private fun makeItem(): ItemStack {
        val id = UUID.randomUUID().toString()
        val item = ItemStack(material)
        val meta = item.itemMeta ?: return item
        meta.setDisplayName(name)
        meta.lore = mutableListOf("${ChatColor.AQUA}id:$id")
        meta.persistentDataContainer.set(idKey, PersistentDataType.STRING, id)
        item.itemMeta = meta
        return item
    }

    fun getBagID(item: ItemStack): String? {
        val meta = item.itemMeta ?: return null
        return meta.persistentDataContainer.get(idKey, PersistentDataType.STRING)
    }
}
