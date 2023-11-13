package com.mcstarrysky.treasure.database

import com.mcstarrysky.starrysky.i18n.I18n
import com.mcstarrysky.treasure.database.impl.PDCDatabase
import org.bukkit.entity.Player

/**
 * Treasure
 * com.mcstarrysky.treasure.database.PlayerDatabase
 *
 * @author 米擦亮
 * @date 2023/11/11 16:32
 */
interface PlayerDatabase {

    fun initialize()

    fun insert(player: Player, key: String, value: Any)

    fun get(player: Player, key: String): String?

    companion object {

        private lateinit var instance: PlayerDatabase

        fun initialize() {
            instance = PDCDatabase()
            instance.initialize()
        }

        fun Player.insert(key: String, value: Any) {
            if (!::instance.isInitialized) {
                I18n.printStackTrace(IllegalStateException("database is not initialized"))
                return
            }
            instance.insert(this, key, value)
        }

        fun Player.get(key: String): String? {
            if (!::instance.isInitialized) {
                I18n.printStackTrace(IllegalStateException("database is not initialized"))
                return null
            }
            return instance.get(this, key)
        }
    }
}