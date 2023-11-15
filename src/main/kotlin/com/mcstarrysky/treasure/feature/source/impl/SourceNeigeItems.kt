package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.treasure.feature.source.Source
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pers.neige.neigeitems.manager.ItemManager
import taboolib.common.platform.function.warning

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.source.impl.SourceNeigeItems
 *
 * @author 米擦亮
 * @date 2023/11/15 21:03
 */
class SourceNeigeItems : Source {

    override val name: String
        get() = "ni"

    override val pluginName: String
        get() = "NeigeItems"

    override val isLoaded: Boolean
        get() = Bukkit.getPluginManager().getPlugin("NeigeItems") != null

    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        // 先获取物品
        val source = ItemManager.getItemStack(value, if (player != null) Bukkit.getOfflinePlayer(player.uniqueId) else null) ?: emptyItemStack.also {
            warning("Error occurred while building *NeigeItems* item *$value* (Item not found)")
        }
        return modify(source, config, player)
    }
}