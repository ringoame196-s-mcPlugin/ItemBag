package com.github.ringoame196_s_mcPlugin.manager

import com.github.ringoame196_s_mcPlugin.data.DatabaseConst
import com.github.ringoame196_s_mcPlugin.data.DatabaseManager
import org.bukkit.plugin.Plugin
import java.security.MessageDigest

class ItemBagPasswordManager(plugin: Plugin) {
    private val table = DatabaseConst.PASSWORDS_TABLE
    private val dataBaseManager = DatabaseManager(plugin)

    fun set(id: String, password: String) {
        val hashPassword = hashSHA256(password)
        val sql = "INSERT INTO $table (${DatabaseConst.ID_KEY}, ${DatabaseConst.PASS_KEY}) VALUES (?, ?) ON CONFLICT(${DatabaseConst.ID_KEY}) DO UPDATE SET ${DatabaseConst.PASS_KEY} = excluded.${DatabaseConst.PASS_KEY};"
        dataBaseManager.executeUpdate(sql, mutableListOf(id, hashPassword))
    }

    fun delete(id: String) {
        val sql = "DELETE FROM $table WHERE ${DatabaseConst.ID_KEY} = ?;"
        dataBaseManager.executeUpdate(sql, mutableListOf(id))
    }

    fun auth(id: String, password: String?): Boolean {
        if (!isLock(id) && password == null) return true
        if (!isLock(id)) return true
        password ?: return false
        val hashPassword = hashSHA256(password)
        val sql = "SELECT EXISTS(SELECT 1 FROM $table WHERE ${DatabaseConst.ID_KEY} = ? AND ${DatabaseConst.PASS_KEY} = ?) AS exists_flag;"
        val result = dataBaseManager.acquisitionValue(sql, mutableListOf(id, hashPassword), "exists_flag")
        return when (result) {
            is Boolean -> result
            is Int -> result != 0
            is Long -> result != 0L
            is String -> result == "1" || result.equals("true", ignoreCase = true)
            else -> false
        }
        return true
    }

    fun isLock(id: String): Boolean {
        val sql = "SELECT EXISTS(SELECT 1 FROM $table WHERE ${DatabaseConst.ID_KEY} = ?) AS exists_flag;"
        val result = dataBaseManager.acquisitionValue(sql, mutableListOf(id), "exists_flag")
        return when (result) {
            is Boolean -> result
            is Int -> result != 0
            is Long -> result != 0L
            is String -> result == "1" || result.equals("true", ignoreCase = true)
            else -> false
        }
    }

    private fun hashSHA256(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) } // 16進文字列に変換
    }
}
