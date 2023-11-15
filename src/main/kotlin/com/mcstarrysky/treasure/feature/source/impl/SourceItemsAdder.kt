package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.treasure.feature.source.Source
import dev.lone.itemsadder.api.CustomStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.source.impl.SourceItemsAdder
 *
 * @author 米擦亮
 * @date 2023/11/15 00:02
 */
class SourceItemsAdder : Source {

    override val name: String
        get() = "ia"

    override val pluginName: String
        get() = "ItemsAdder"

    override val isLoaded: Boolean
        get() = Bukkit.getPluginManager().getPlugin("ItemsAdder") != null

    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        // 先获取物品
        val source = CustomStack.getInstance(value)?.itemStack ?: return emptyItemStack.also {
            warning("Error occurred while building *ItemsAdder* item *$value* (Item not found)")
        }
        return modify(source, config, player)
    }
}