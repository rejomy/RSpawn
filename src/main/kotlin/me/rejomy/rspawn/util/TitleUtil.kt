package me.rejomy.rspawn.util

import me.rejomy.rspawn.hook.impl.PacketEventsHook
import org.bukkit.entity.Player

object TitleUtil {

    fun displayTitle(player: Player, header: String, footer: String, fadeIn: Int, time: Int, fadeOut: Int) {
        if (PacketEventsHook.enable) {
            PacketEventsHook.packetEventsAPI.playerManager.getUser(player).sendTitle(header, footer, fadeOut, time, fadeIn)
        } else {
            if (ServerVersionUtil.newerThan18()) {
                player.sendTitle(header, footer, fadeIn, time, fadeOut)
            } else {
                // 1.8 does not support titles with customized fadeIn/fadeOut time in bukkit api.
                player.sendTitle(header, footer)
            }
        }
    }

    fun displayTitle(player: Player, header: String, footer: String) {
        displayTitle(player, header, footer, 5, 40, 5)
    }
}