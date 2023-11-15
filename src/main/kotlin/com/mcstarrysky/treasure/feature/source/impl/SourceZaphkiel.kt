package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.starrysky.utils.replace
import com.mcstarrysky.treasure.feature.source.Source
import com.mcstarrysky.treasure.library.xseries.XItemStack
import com.mcstarrysky.treasure.utils.replacePlaceholder
import ink.ptms.zaphkiel.Zaphkiel
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.variable
import taboolib.common.platform.function.warning
import java.util.function.Function

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

    // FIXME: 这里在考虑要不要把所有判断都改成直接反射检测类, 先这样吧
    override val isLoaded: Boolean
        get() = Bukkit.getPluginManager().getPlugin("Zaphkiel") != null

    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        // 先获取物品
        val source = Zaphkiel.api().getItemManager().generateItemStack(value, player) ?: emptyItemStack.also {
            warning("Error occurred while building *Zaphkiel* item *$value* (Item not found)")
        }
        // 再配置好物品信息, 配置好新旧 Lore
        val conf = XItemStack.mapToConfigSection(mutableMapOf<String, Any>().also { it.putAll(config) }).also {
            it["name"] = (it.getString("name") ?: "").replace("name" to source.itemMeta?.displayName.toString())
            it["lore"] = it.getStringList("lore").variable("lore", source.itemMeta?.lore ?: emptyList())
        }
        // 通过反序列化得到最终物品
        val result = XItemStack.edit(source, conf, Function.identity(), null)
        // 替换 PlaceholderAPI 变量
        player?.let { result.replacePlaceholder(it) }
        return result
    }
}