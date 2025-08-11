package com.github.ringoame196_s_mcPlugin

import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

class ItemStorageManager(plugin: Plugin) {
    private val dataBaseManager = DataBaseManager(plugin)
    private val table = DataBaseConst.ITEMS_TABLE

    fun save(id: String, inventory: Inventory) {
        for (slot in 0..inventory.size - 1) {
            val item = inventory.getItem(slot)
            val sql: String
            val parameters: List<Any>
            if (item != null) {
                sql = "INSERT INTO $table (${DataBaseConst.ID_KEY}, ${DataBaseConst.SLOT_KEY}, ${DataBaseConst.ITEM_KEY}) VALUES (?, ?, ?) ON CONFLICT(${DataBaseConst.ID_KEY}, ${DataBaseConst.SLOT_KEY}) DO UPDATE SET ${DataBaseConst.ITEM_KEY} = excluded.${DataBaseConst.ITEM_KEY};"
                val itemData = ItemStackSerializer.itemStackToBase64(item)
                parameters = listOf(id, slot, itemData)
            } else {
                sql = "DELETE FROM $table WHERE ${DataBaseConst.ID_KEY} = ? AND ${DataBaseConst.SLOT_KEY} = ?;"
                parameters = listOf(id, slot)
            }
            dataBaseManager.executeUpdate(sql, parameters)
        }
    }

    fun load(id: String, inventory: Inventory) {
        val sql = "SELECT * FROM $table WHERE ${DataBaseConst.ID_KEY} = ?;"
        val tableData = dataBaseManager.acquisitionValuesList(sql, mutableListOf(id), mutableListOf(DataBaseConst.SLOT_KEY, DataBaseConst.ITEM_KEY))

        for (data in tableData) {
            val slot = data[DataBaseConst.SLOT_KEY].toString().toInt()
            val itemBase64 = data[DataBaseConst.ITEM_KEY].toString()
            val item = ItemStackSerializer.itemStackFromBase64(itemBase64)
            inventory.setItem(slot, item)
        }
    }
}
