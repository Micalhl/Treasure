package com.mcstarrysky.treasure.feature.location

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import taboolib.common.util.unsafeLazy
import taboolib.common5.cdouble

/**
 * Treasure
 * com.mcstarrysky.treasure.feature.location.Position
 *
 * @author 米擦亮
 * @date 2023/11/11 16:21
 */
data class Position(
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val blockFace: BlockFace
) {

    @delegate:Transient
    val bukkitLocation: Location by unsafeLazy {
        Location(Bukkit.getWorld(world), x.cdouble, y.cdouble, z.cdouble)
    }
}
