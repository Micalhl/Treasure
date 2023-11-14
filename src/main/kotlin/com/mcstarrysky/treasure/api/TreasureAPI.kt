package com.mcstarrysky.treasure.api

import com.mcstarrysky.treasure.feature.TreasureChest
import com.mcstarrysky.treasure.feature.source.Source
import org.bukkit.entity.Player
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import java.util.concurrent.CompletableFuture

/**
 * Treasure
 * com.mcstarrysky.treasure.api.TreasureAPI
 *
 * @author 米擦亮
 * @date 2023/11/14 22:03
 */
object TreasureAPI {

    /**
     * 获取物品源
     */
    fun getItemSource(name: String): Source? {
        return Source.sources[name]
    }

    /**
     * 获取宝箱数据
     */
    fun getTreasureChest(name: String): TreasureChest? {
        return TreasureChest.treasures[name]
    }

    fun runKether(player: Player, script: String): CompletableFuture<Any?> {
        return KetherShell.eval(script, ScriptOptions.new {
            sender(player)
            detailError(true)
        })
    }

    fun runKether(player: Player, scripts: List<String>): CompletableFuture<Any?> {
        return KetherShell.eval(scripts, ScriptOptions.new {
            sender(player)
            detailError(true)
        })
    }
}