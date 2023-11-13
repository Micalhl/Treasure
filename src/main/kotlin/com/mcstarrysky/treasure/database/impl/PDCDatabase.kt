package com.mcstarrysky.treasure.database.impl

import com.mcstarrysky.starrysky.utils.get
import com.mcstarrysky.starrysky.utils.set
import com.mcstarrysky.treasure.database.PlayerDatabase
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

/**
 * Treasure
 * com.mcstarrysky.treasure.database.impl.PDCDatabase
 *
 * @author 米擦亮
 * @date 2023/11/11 16:33
 */
class PDCDatabase : PlayerDatabase {

    override fun initialize() {
    }

    override fun insert(player: Player, key: String, value: Any) {
        player[key, PersistentDataType.STRING] = value.toString()
    }

    override fun get(player: Player, key: String): String? {
        return player[key, PersistentDataType.STRING]
    }
}