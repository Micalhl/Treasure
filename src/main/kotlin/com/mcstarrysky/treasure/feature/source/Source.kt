package com.mcstarrysky.treasure.feature.source

import com.mcstarrysky.starrysky.i18n.sendLang
import com.mcstarrysky.starrysky.utils.replace
import com.mcstarrysky.treasure.feature.source.impl.*
import com.mcstarrysky.treasure.library.xseries.XItemStack
import com.mcstarrysky.treasure.utils.replacePlaceholder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.variable
import org.serverct.parrot.parrotx.mechanism.Reloadable
import taboolib.common.LifeCycle
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.source.Source
 *
 * @author 米擦亮
 * @date 2023/11/14 21:24
 */
interface Source {

    val name: String

    val pluginName: String

    val isLoaded: Boolean

    fun build(config: Map<String, Any>, value: String, player: Player?): ItemStack

    /**
     * 通用的根据配置读取替换物品信息的函数
     * 提取一下公因式吧
     */
    fun modify(source: ItemStack, config: Map<String, Any>, player: Player?): ItemStack {
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

    companion object {

        val sources = ConcurrentHashMap<String, Source>()

        @Reloadable
        @Awake(LifeCycle.ENABLE)
        fun register() {
            listOf(
                SourceItemsAdder(),
                SourceMMOItems(),
                SourceMythic(),
                SourceNeigeItems(),
                SourceSplendidEnchants(),
                SourceZaphkiel()
            ).forEach(Source::register)
        }

        fun register(instance: Source) {
            if (sources.contains(instance.name)) {
                console().sendLang("source.exist", "plugin" to instance.pluginName)
                return
            }
            if (instance.isLoaded) {
                sources += instance.name to instance
                console().sendLang("source.done", "plugin" to instance.pluginName)
            }
        }

        /* FIXME: 无法实现
        @Awake(LifeCycle.ENABLE)
        fun inject() {
            runningClasses.filter { Source::class.java.isAssignableFrom(it) }
                .forEach {
                    val instance = it.unsafeInstance() as Source
                    sources += instance.name to instance
                }
        }
         */
    }
}