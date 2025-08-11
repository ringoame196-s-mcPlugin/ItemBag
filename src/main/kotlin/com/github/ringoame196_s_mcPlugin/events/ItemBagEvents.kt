package com.github.ringoame196_s_mcPlugin.events

import com.github.ringoame196_s_mcPlugin.ItemBagManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

class ItemBagEvents(plugin: Plugin) : Listener {
    private val itemBagManager = ItemBagManager(plugin)

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val item = e.item ?: return
        val bagID = itemBagManager.getBagID(item) ?: return
        e.isCancelled = true
        itemBagManager.openInv(player, bagID)
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val player = e.player
        val inventory = e.inventory
        val viewTitle = e.view.title
        if (!itemBagManager.isBagInventory(viewTitle)) return
        itemBagManager.closeInv(player as Player, inventory)
    }
}
