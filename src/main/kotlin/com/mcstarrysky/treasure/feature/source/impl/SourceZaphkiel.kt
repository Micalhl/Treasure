package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.treasure.feature.source.Source
import ink.ptms.zaphkiel.Zaphkiel
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.source.impl.SourceZaphkiel
 *
 * @author 米擦亮
 * @date 2023/11/14 21:42
 */
class SourceZaphkiel : Source {

    override val name: String
        get() = "zaphkiel"

    override val pluginName: String
        get() = "Zaphkiel"

    override val isLoaded: Boolean
        get() = Bukkit.getPluginManager().getPlugin("Zaphkiel") != null

    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        return Zaphkiel.api().getItemManager().generateItemStack(value, player) ?: emptyItemStack.also {
            warning("Error occurred while building *Zaphkiel* item *$value* (Item not found)")
        }
    }
}