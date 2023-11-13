@file:Suppress("deprecation")
package com.mcstarrysky.treasure.api

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Chest
import org.bukkit.entity.Player
import taboolib.common5.cbyte
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.dataSerializerBuilder
import taboolib.module.nms.nmsClass
import taboolib.module.nms.sendPacket
import taboolib.platform.util.isNotAir

/**
 * Treasure
 * com.mcstarrysky.treasure.api.NMS
 *
 * @author 米擦亮
 * @date 2023/11/11 16:53
 */
abstract class NMS {

    abstract fun sendBlockAction(user: Player, location: Location, actionId: Int, actionParameter: Int)

    abstract fun sendBlockChange(user: Player, location: Location, block: Material, blockFace: BlockFace)
}

class NMSImpl : NMS() {

    private val facingMap = mapOf(BlockFace.NORTH to 2, BlockFace.SOUTH to 3, BlockFace.WEST to 4, BlockFace.EAST to 5)

    override fun sendBlockAction(user: Player, location: Location, actionId: Int, actionParameter: Int) {
        if (MinecraftVersion.isUniversal) {
            user.sendPacket(NMSPacketPlayOutBlockAction(dataSerializerBuilder {
                writeBlockPosition(location.blockX, location.blockY, location.blockZ)
                writeByte(actionId.cbyte)
                writeByte(actionParameter.cbyte)
                //writeVarInt(NMSIRegistry.BLOCK.getId(NMSBlocks.CHEST))
                writeVarInt(nmsClass(if (MinecraftVersion.majorLegacy >= 11903) "BuiltInRegistries" else "IRegistry").getProperty<Any>("BLOCK", isStatic = true)?.invokeMethod<Int>("getId", nmsClass("Blocks").getProperty<Any>("CHEST", isStatic = true)!!)!!)
            }.build() as NMSPacketDataSerializer).also { println(it) })
        } else {
            user.sendPacket(NMS16PacketPlayOutBlockAction().a(dataSerializerBuilder {
                writeBlockPosition(location.blockX, location.blockY, location.blockZ)
                writeByte(actionId.cbyte)
                writeByte(actionParameter.cbyte)
                //writeVarInt(NMS16IRegistry.BLOCK.a(NMS16Blocks.CHEST))
                writeVarInt(nmsClass("IRegistry").getProperty<Any>("BLOCK", isStatic = true)?.invokeMethod<Int>("a", nmsClass("Blocks").getProperty<Any>("CHEST", isStatic = true)!!)!!)
            }.build() as NMS16PacketDataSerializer))
        }
    }

    override fun sendBlockChange(user: Player, location: Location, block: Material, blockFace: BlockFace) {
        if (MinecraftVersion.isLowerOrEqual(MinecraftVersion.V1_12)) {
            if (block.isNotAir()) {
                user.sendBlockChange(location, block, facingMap[blockFace]?.toByte() ?: return)
            } else {
                user.sendBlockChange(location, Material.AIR, 0)
            }
        } else {
            if (block.isNotAir()) {
                user.sendBlockChange(location, block.createBlockData {
                    (it as? Chest)?.facing = blockFace
                })
            } else {
                user.sendBlockChange(location, Material.AIR.createBlockData())
            }
        }
    }
}