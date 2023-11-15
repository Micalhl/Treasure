package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.treasure.feature.source.Source
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning
import taboolib.common5.cint
import world.icebear03.splendidenchants.api.book
import world.icebear03.splendidenchants.api.drawEt
import world.icebear03.splendidenchants.enchant.data.defaultRarity
import world.icebear03.splendidenchants.enchant.data.rarity

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.source.impl.SourceSplendidEnchants
 *
 * @author 米擦亮
 * @date 2023/11/15 21:30
 */
class SourceSplendidEnchants : Source {

    override val name: String
        get() = "se"

    override val pluginName: String
        get() = "SplendidEnchants"

    override val isLoaded: Boolean
        get() = Bukkit.getPluginManager().getPlugin("SplendidEnchants") != null

    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        val (rarity, level) = value.split(",", limit = 2)
        return (rarity(rarity) ?: defaultRarity).drawEt()?.book(level.cint) ?: emptyItemStack.also {
            warning("Error occurred while building *SplendidEnchants* random enchant book *$value* (Unknown error)")
        }
    }
}