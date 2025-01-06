package me.rejomy.rspawn.util

import org.bukkit.entity.Player

object PlayerUtil {

    fun clearEffects(player: Player) {
        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }
    }

    fun resetVariables(player: Player) {
        player.foodLevel = 20
        player.health = player.maxHealth
        player.exp = 0F
    }
}