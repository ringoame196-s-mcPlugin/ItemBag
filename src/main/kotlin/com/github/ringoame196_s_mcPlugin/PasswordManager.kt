package com.github.ringoame196_s_mcPlugin

import org.bukkit.plugin.Plugin
import java.security.MessageDigest

class PasswordManager(plugin: Plugin) {
    private val dbFilePath = "${plugin.dataFolder.path}/data.db"
    private val table = DataBaseConst.PASSWORDS_TABLE
    private val dataBaseManager = DataBaseManager(dbFilePath)

    fun set(id: String, password: String) {
        val hashPassword = hashSHA256(password)
        val sql = "INSERT INTO $table (${DataBaseConst.ID_KEY}, ${DataBaseConst.PASS_KEY}) VALUES (?, ?) ON CONFLICT(${DataBaseConst.ID_KEY}) DO UPDATE SET ${DataBaseConst.PASS_KEY} = excluded.${DataBaseConst.PASS_KEY};"
        dataBaseManager.executeUpdate(sql, mutableListOf(id, hashPassword))
    }

    fun auth(id: String, password: String): Boolean {
        val hashPassword = hashSHA256(password)
        return true
    }

    fun isLock(id: String): Boolean {
        return true
    }

    private fun hashSHA256(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) } // 16進文字列に変換
    }
}
