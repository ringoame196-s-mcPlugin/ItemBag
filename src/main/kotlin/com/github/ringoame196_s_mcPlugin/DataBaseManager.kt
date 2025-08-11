package com.github.ringoame196_s_mcPlugin

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class DataBaseManager(plugin: Plugin) {
    private val dbFilePath = "${plugin.dataFolder.path}/data.db"

    /**
     * SQLコマンドを実行する
     * @param command 実行するSQL文
     * @param parameters パラメータリスト
     */
    fun executeUpdate(command: String, parameters: List<Any>? = null) {
        try {
            connection.use { conn ->
                conn.prepareStatement(command).use { preparedStatement ->
                    parameters?.bindParameters(preparedStatement)
                    preparedStatement.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            Bukkit.getLogger().info("SQL Error: ${e.message}")
            throw e
        }
    }

    /**
     * 単一の値を取得する
     * @param sql 実行するSQL文
     * @param parameters パラメータリスト
     * @param label カラム名
     * @return 結果の値
     */
    fun acquisitionValue(sql: String, parameters: List<Any>, label: String): Any? {
        return acquisitionValues(sql, parameters, mutableListOf(label)).getValue(label)
    }

    /**
     * 複数の行を取得する
     * @param sql 実行するSQL文
     * @param parameters パラメータリスト
     * @param mapper 結果セットの行をオブジェクトにマッピングする関数
     * @return 結果リスト
     */
    fun acquisitionValues(
        sql: String,
        parameters: List<Any>,
        keys: List<String>
    ): Map<String, Any?> {
        try {
            val values = mutableMapOf<String, Any?>()
            connection.use { conn ->
                conn.prepareStatement(sql).use { preparedStatement ->
                    parameters.bindParameters(preparedStatement)
                    preparedStatement.executeQuery().use { resultSet ->
                        if (resultSet.next()) {
                            for (key in keys) {
                                values[key] = try {
                                    resultSet.getString(key)
                                } catch (e: SQLException) {
                                    null
                                }
                            }
                        }
                    }
                }
            }
            return values
        } catch (e: SQLException) {
            Bukkit.getLogger().info("SQL Error: ${e.message}")
            throw e
            return mapOf()
        }
    }

    fun acquisitionValuesList(
        sql: String,
        parameters: List<Any> = emptyList(),
        keys: List<String>
    ): List<Map<String, Any?>> {
        try {
            val results = mutableListOf<Map<String, Any?>>()

            connection.use { conn ->
                conn.prepareStatement(sql).use { preparedStatement ->
                    parameters.bindParameters(preparedStatement)
                    preparedStatement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val row = mutableMapOf<String, Any?>()
                            for (key in keys) {
                                row[key] = try {
                                    resultSet.getString(key)
                                } catch (e: SQLException) {
                                    null
                                }
                            }
                            results.add(row)
                        }
                    }
                }
            }

            return results
        } catch (e: SQLException) {
            Bukkit.getLogger().info("SQL Error: ${e.message}")
            throw e
        }
    }

    // SQLiteコネクションの取得
    private val connection: Connection
        get() = DriverManager.getConnection("jdbc:sqlite:$dbFilePath")

    // パラメータをPreparedStatementにバインドする拡張関数
    private fun List<Any>.bindParameters(preparedStatement: PreparedStatement) {
        this.forEachIndexed { index, param ->
            preparedStatement.setObject(index + 1, param)
        }
    }
}
