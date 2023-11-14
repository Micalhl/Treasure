package com.mcstarrysky.treasure.feature

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mcstarrysky.treasure.feature.source.Source
import com.mcstarrysky.treasure.utils.replacePlaceholder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.configuration.Conversion
import taboolib.library.xseries.XItemStack
import taboolib.module.chat.component
import taboolib.module.configuration.MapConverter
import taboolib.module.nms.setDisplayName
import taboolib.module.nms.setLore
import taboolib.platform.util.modifyMeta

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.TreasureIcon
 *
 * @author 米擦亮
 * @date 2023/11/11 12:52
 */
data class TreasureIcon(
    @Conversion(MapConverter::class)
    val item: Map<String, Any>, // 物品配置
    val chance: Double // 每次出现物品的概率
) {

    fun build(player: Player?): ItemStack {
        val mats = item["material"].toString()
        val build = runCatching {
            XItemStack.deserialize(item).modifyMeta<ItemMeta> {
                setDisplayName(displayName.component().buildColored())
                setLore(lore?.map { it.component().buildColored() } ?: emptyList())
            }
        }.getOrNull().also { item -> player?.let { item?.replacePlaceholder(it) } }
        if (!mats.startsWith("source")) {
            return build ?: emptyItemStack
        }
        val (_, type, value) = mats.split(":", limit = 3).map(String::lowercase)
        return Source.sources[type]?.build(item, value, player) ?: build ?: emptyItemStack
    }
}
