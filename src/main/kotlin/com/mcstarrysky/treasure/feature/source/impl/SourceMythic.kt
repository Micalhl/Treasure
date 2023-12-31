package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.treasure.feature.source.Source
import ink.ptms.um.Mythic
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.source.impl.SourceMythic
 *
 * @author 米擦亮
 * @date 2023/11/14 21:35
 */
class SourceMythic : Source {

    override val name: String
        get() = "mythic"

    override val pluginName: String
        get() = "MythicMobs"

    override val isLoaded: Boolean
        get() = Mythic.isLoaded()

    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        // 先获取物品
        val source = Mythic.API.getItem(value)?.generateItemStack(1) ?: emptyItemStack.also {
            warning("Error occurred while building *MythicMobs* item *$value* (Item not found)")
        }
        return modify(source, config, player)
    }
}