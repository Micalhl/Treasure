package com.mcstarrysky.treasure.feature

import com.mcstarrysky.treasure.feature.location.Position
import com.mcstarrysky.treasure.utils.ListPositionConverter
import com.mcstarrysky.treasure.utils.TreasureIconMapConverter
import com.mcstarrysky.treasure.utils.shuffleChars
import org.bukkit.entity.Player
import taboolib.common.io.newFolder
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.randomDouble
import taboolib.library.configuration.Conversion
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.TreasureChest
 *
 * @author 米擦亮
 * @date 2023/11/11 12:36
 */
data class TreasureChest(
    val title: String, // 标题
    val layout: List<String>, // 布局
    val shuffled: Boolean, // 随机打乱的视图
    @Conversion(TreasureIconMapConverter::class)
    val icons: Map<String, TreasureIcon>, // 图标
    @Conversion(ListPositionConverter::class)
    val locations: List<Position>
) {

    fun open(player: Player) {
        player.openMenu<Basic>(title) {
            rows(layout.size)
            map(*(if (shuffled) layout.shuffleChars() else layout).toTypedArray())

            onBuild { _, inventory ->
                icons.forEach { (char, icon) ->
                    for (slot in getSlots(char[0])) {
                        if (randomDouble() <= icon.chance) {
                            inventory.setItem(slot, icon.build(player))
                        }
                    }
                }
            }
        }
    }

    companion object {

        val treasures = ConcurrentHashMap<String, TreasureChest>()

        fun initialize() {
            treasures.clear()
            if (!File(getDataFolder(), "treasures").exists()) {
                releaseResourceFile("treasures/Example.yml")
            }
            newFolder(getDataFolder(), "treasures")
                .listFiles { file -> file.extension == "yml" }
                ?.forEach { file ->
                    treasures += file.nameWithoutExtension to Configuration.deserialize(Configuration.loadFromFile(file), true)
                }
        }
    }
}