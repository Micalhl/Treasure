package com.mcstarrysky.treasure.feature.source.impl

import com.mcstarrysky.treasure.feature.source.Source
import dev.lone.itemsadder.api.CustomStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.xseries.XItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.component
import taboolib.module.nms.setDisplayName
import taboolib.module.nms.setLore
import taboolib.platform.util.hasLore
import taboolib.platform.util.hasName
import taboolib.platform.util.modifyLore
import taboolib.platform.util.modifyMeta

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
        val clone = hashMapOf<String, Any>().also { it.putAll(config) }
        val ia = CustomStack.getInstance(value)?.itemStack ?: return ItemStack(Material.BEDROCK)
        val item = XItemStack.deserialize(clone)
        ia.modifyMeta<ItemMeta> {
            if (item.hasName()) {
                setDisplayName(item.itemMeta!!.displayName)
            }
            if (item.hasLore()) {
                modifyLore { ia.itemMeta?.lore?.plusAssign(item.itemMeta?.lore ?: emptyList()) }
            }
        }
        return ia
    }

    /*
    override fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack {
        // 克隆一份原配置
        val clone = hashMapOf<String, Any>().also { it.putAll(config) }
        // 设置物品材质为原版
        clone["material"] = "STONE"
        // 先临时构建出一份物品
        val item = XItemStack.deserialize(clone).modifyMeta<ItemMeta> {
            setDisplayName(displayName.component().buildColored())
            setLore(lore?.map { it.component().buildColored() } ?: emptyList())
        }
        val ia = CustomStack.getInstance(value)
        // TODO & FIXME: 一般是只要显示图标, 这里有一个不太成熟的方案待修正
        item.type = ia?.itemStack?.type ?: item.type
        item.modifyMeta<ItemMeta> {
            setCustomModelData(ia?.itemStack?.itemMeta?.customModelData ?: customModelData)
        }
        return item
    }

     */
}