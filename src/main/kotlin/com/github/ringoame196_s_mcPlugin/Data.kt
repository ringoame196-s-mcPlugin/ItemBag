package com.github.ringoame196_s_mcPlugin

import org.bukkit.inventory.Inventory
import java.util.UUID

object Data {
    val bagCashInventory = mutableMapOf<String, Inventory>()
    val openBagInventory = mutableMapOf<UUID, String>()
}
