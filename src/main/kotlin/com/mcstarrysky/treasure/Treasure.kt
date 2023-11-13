package com.mcstarrysky.treasure

import com.mcstarrysky.starrysky.AbstractPlugin
import com.mcstarrysky.treasure.database.PlayerDatabase
import com.mcstarrysky.treasure.feature.TreasureChest
import taboolib.module.ui.enableRawTitleInVanillaInventory

/**
 * Treasure
 * com.mcstarrysky.treasure.Treasure
 *
 * @author 米擦亮
 * @date 2023/11/11 13:56
 */
object Treasure : AbstractPlugin() {

    override fun preload() {
        enableRawTitleInVanillaInventory()
        this.config = TreasureConfig.config
    }

    override fun load() {
        PlayerDatabase.initialize()
        TreasureChest.initialize()
    }
}