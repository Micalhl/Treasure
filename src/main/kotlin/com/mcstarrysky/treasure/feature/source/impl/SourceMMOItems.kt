package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.starrysky.utils.replace
import com.mcstarrysky.treasure.feature.source.Source
import com.mcstarrysky.treasure.library.xseries.XItemStack
import com.mcstarrysky.treasure.utils.replacePlaceholder
import net.Indyuce.mmoitems.MMOItems
import net.Indyuce.mmoitems.api.Type
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.variable
import taboolib.common.platform.function.warning
import java.util.function.Function

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