package com.github.ringoame196_s_mcPlugin.data

import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

class ItemStorage(plugin: Plugin) {
    private val dataBaseManager = DatabaseManager(plugin)
    private val table = DatabaseConst.ITEMS_TABLE

    fun save(id: String, inventory: Inventory) {
        for (slot in 0..inventory.size - 1) {
            val item = inventory.getItem(slot)
            val sql: String
            val parameters: List<Any>
            if (item != null) {
                sql = "INSERT INTO $table (${DatabaseConst.ID_KEY}, ${DatabaseConst.SLOT_KEY}, ${DatabaseConst.ITEM_KEY}) VALUES (?, ?, ?) ON CONFLICT(${DatabaseConst.ID_KEY}, ${DatabaseConst.SLOT_KEY}) DO UPDATE SET ${DatabaseConst.ITEM_KEY} = excluded.${DatabaseConst.ITEM_KEY};"
                val itemData = ItemStackSerializer.itemStackToBase64(item)
                parameters = listOf(id, slot, itemData)
            } else {
                sql = "DELETE FROM $table WHERE ${DatabaseConst.ID_KEY} = ? AND ${DatabaseConst.SLOT_KEY} = ?;"
                parameters = listOf(id, slot)
            }
            dataBaseManager.executeUpdate(sql, parameters)
        }
    }

    fun load(id: String, inventory: Inventory) {
        val sql = "SELECT * FROM $table WHERE ${DatabaseConst.ID_KEY} = ?;"
        val tableData = dataBaseManager.acquisitionValuesList(sql, mutableListOf(id), mutableListOf(DatabaseConst.SLOT_KEY, DatabaseConst.ITEM_KEY))

        for (data in tableData) {
            val slot = data[DatabaseConst.SLOT_KEY].toString().toInt()
            val itemBase64 = data[DatabaseConst.ITEM_KEY].toString()
            val item = ItemStackSerializer.itemStackFromBase64(itemBase64)
            inventory.setItem(slot, item)
        }
    }
}
