package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.treasure.feature.source.Source
import net.Indyuce.mmoitems.MMOItems
import net.Indyuce.mmoitems.api.Type
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.source.impl.SourceMMOItems
 *
 * @author 米擦亮
 * @date 2023/11/14 23:50
 */
class SourceMMOItems : Source {

    override val name: String
        get() = "mmoitems"

    override val pluginName: String
        get() = "MMOItems"

    override val isLoaded: Boolean
        get() = Bukkit.getPluginManager().getPlugin("MMOItems") != null

    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        // 类型, 物品 ID
        val (type, name) = value.split(",", limit = 2)
        // 先获取物品 FIXME: Player?
        val source = MMOItems.plugin.getItem(Type.get(type), name) ?: emptyItemStack.also {
            warning("Error occurred while building *MMOItems* item *$value* (Item not found)")
        }
        return modify(source, config, player)
    }
}