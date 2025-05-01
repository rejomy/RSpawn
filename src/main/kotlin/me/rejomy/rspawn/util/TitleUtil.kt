package me.rejomy.rspawn.util

import org.bukkit.entity.Player

object TitleUtil {

    fun displayTitle(player: Player, header: String, footer: String, fadeIn: Int, time: Int, fadeOut: Int) {
        player.sendTitle(header, footer, fadeIn, time, fadeOut)
    }

    fun displayTitle(player: Player, header: String, footer: String) {
        displayTitle(player, header, footer, 5, 40, 5)
    }
}