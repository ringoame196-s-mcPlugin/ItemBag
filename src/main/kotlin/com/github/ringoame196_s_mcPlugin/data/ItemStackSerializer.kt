package com.github.ringoame196_s_mcPlugin.data

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64

object ItemStackSerializer {
    fun itemStackToBase64(item: ItemStack): String {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)
        dataOutput.writeObject(item)
        dataOutput.close()
        return Base64.getEncoder().encodeToString(outputStream.toByteArray())
    }

    fun itemStackFromBase64(data: String): ItemStack {
        val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val item = dataInput.readObject() as ItemStack
        dataInput.close()
        return item
    }
}
