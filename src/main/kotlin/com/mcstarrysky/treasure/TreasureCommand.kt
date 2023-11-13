package com.mcstarrysky.treasure

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.command.CommandHandler
import com.mcstarrysky.starrysky.command.executeAsBukkitPlayer
import com.mcstarrysky.treasure.api.NMS
import com.mcstarrysky.treasure.feature.TreasureChest
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.common5.cint
import taboolib.module.nms.nmsProxy
import java.util.concurrent.ConcurrentHashMap

/**
 * Treasure
 * com.mcstarrysky.treasure.TreasureCommand
 *
 * @author 米擦亮
 * @date 2023/11/11 14:11
 */
@CommandHeader(name = "treasure")
object TreasureCommand : CommandHandler {

    override val sub: ConcurrentHashMap<String, CommandExecutor> = ConcurrentHashMap()

    @CommandBody
    val main = mainCommand {
        createTabooLibLegacyHelper()
    }

    @CommandBody
    val test = object : CommandExecutor {

        override val command: SimpleCommandBody
            get() = subCommand {
                dynamic {
                    suggestionUncheck<Player> { sender, context ->
                        TreasureChest.treasures.keys().toList()
                    }
                    executeAsBukkitPlayer { p, _, c ->
                        TreasureChest.treasures[c]?.open(p)
                    }
                }
            }
        override val name: String
            get() = "test"
        init {
            sub[name] = this
        }
    }.command

    // 1,1 开
    // 1,0 关
    @CommandBody
    val action = object : CommandExecutor {

        override val command: SimpleCommandBody
            get() = subCommand {
                dynamic {
                    executeAsBukkitPlayer { p, context, argument ->
                        println(p.getTargetBlockExact(5)?.type)
                        nmsProxy<NMS>().sendBlockAction(p, p.getTargetBlockExact(5)?.location ?: return@executeAsBukkitPlayer, 1, argument.cint)
                    }
                }
            }
        override val name: String
            get() = "action"
        init {
            sub[name]=this
        }
    }.command

    @CommandBody
    val change = object : CommandExecutor {
        override val command: SimpleCommandBody
            get() = subCommand {
                dynamic {
                    executeAsBukkitPlayer { sender, _, a ->
                        nmsProxy<NMS>().sendBlockChange(sender, sender.location, Material.CHEST, BlockFace.valueOf(a))
                    }
                }
            }
        override val name: String
            get() = "change"
        init {
            sub[name]=this
        }
    }.command
}