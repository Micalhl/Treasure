package com.mcstarrysky.treasure

import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

/**
 * Treasure
 * com.mcstarrysky.treasure.TreasureConfig
 *
 * @author 米擦亮
 * @date 2023/11/11 13:56
 */
object TreasureConfig {

    @Config
    lateinit var config: Configuration
        private set
}