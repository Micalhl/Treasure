package com.mcstarrysky.treasure.feature.source

import com.mcstarrysky.starrysky.i18n.sendLang
import com.mcstarrysky.treasure.feature.source.impl.SourceItemsAdder
import com.mcstarrysky.treasure.feature.source.impl.SourceMMOItems
import com.mcstarrysky.treasure.feature.source.impl.SourceMythic
import com.mcstarrysky.treasure.feature.source.impl.SourceZaphkiel
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.mechanism.Reloadable
import taboolib.common.LifeCycle
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import java.util.concurrent.ConcurrentHashMap

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

    companion object {

        val sources = ConcurrentHashMap<String, Source>()

        @Reloadable
        @Awake(LifeCycle.ENABLE)
        fun register() {
            listOf(
                SourceItemsAdder(),
                SourceMMOItems(),
                SourceMythic(),
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