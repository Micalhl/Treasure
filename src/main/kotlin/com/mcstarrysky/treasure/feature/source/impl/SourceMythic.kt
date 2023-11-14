package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.treasure.feature.source.Source
import ink.ptms.um.Mythic
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning
import taboolib.common5.cint

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

    // FIXME: 这里在考虑要不要把所有判断都改成直接反射检测类, 先这样吧
    override val isLoaded: Boolean
        get() = Bukkit.getPluginManager().getPlugin("MythicMobs") != null

    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        return if (Mythic.isLoaded()) {
            warning("You are using MythicMobs source but MythicMobs plugin is not installed")
            return emptyItemStack
        } else Mythic.API.getItem(value)?.generateItemStack(config["amount"].cint) ?: emptyItemStack.also {
            warning("Error occurred while building *MythicMobs* item *$value* (Item not found)")
        }
    }
}