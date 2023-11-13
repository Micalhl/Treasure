package com.mcstarrysky.treasure.utils

import com.mcstarrysky.starrysky.utils.ListSliceUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.modifyMeta
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.asKotlinRandom

/**
 * Treasure
 * com.mcstarrysky.treasure.utils.Utils
 *
 * @author 米擦亮
 * @date 2023/11/11 13:20
 */
fun List<String>.shuffleChars(): List<String> {
    // val random = ThreadLocalRandom.current().asKotlinRandom()
    // return map { it.toList().shuffled(random).joinToString("") }.shuffled(random)
    return ListSliceUtils.partition(map { it.toList() }.flatten()
        .shuffled(ThreadLocalRandom.current().asKotlinRandom()), 9)
        .map { it.joinToString("") }
}

fun ItemStack.replacePlaceholder(player: Player): ItemStack {
    return clone().modifyMeta<ItemMeta> {
        setDisplayName(displayName.replacePlaceholder(player))
        lore = lore?.replacePlaceholder(player)
    }
}