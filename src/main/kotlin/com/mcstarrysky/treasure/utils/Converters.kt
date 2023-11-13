package com.mcstarrysky.treasure.utils

import com.electronwill.nightconfig.core.AbstractConfig
import com.mcstarrysky.treasure.feature.TreasureIcon
import com.mcstarrysky.treasure.feature.location.Position
import org.bukkit.block.BlockFace
import taboolib.common5.cdouble
import taboolib.common5.cint
import taboolib.library.configuration.Converter
import taboolib.module.configuration.Configuration

/**
 * Treasure
 * com.mcstarrysky.treasure.utils.Converters
 *
 * @author 米擦亮
 * @date 2023/11/11 14:31
 */
class TreasureIconMapConverter : Converter<Map<String, TreasureIcon>, AbstractConfig> {

    override fun convertToField(value: AbstractConfig): Map<String, TreasureIcon> {
        return value.valueMap()
            .mapValues { Configuration.deserialize(Configuration.fromMap((it.value as AbstractConfig).valueMap()), true) }
    }

    override fun convertFromField(p0: Map<String, TreasureIcon>?): AbstractConfig {
        TODO("Not yet implemented")
    }
}

class ListPositionConverter : Converter<List<Position>, List<String>> {

    override fun convertToField(value: List<String>): List<Position> {
        return value.map {
            val (world, loc, face) = it.split("~")
            val (x, y, z) = loc.split(",").map(String::cint)
            return@map Position(world, x, y, z, BlockFace.valueOf(face))
        }
    }

    override fun convertFromField(p0: List<Position>?): List<String> {
        TODO("Not yet implemented")
    }
}