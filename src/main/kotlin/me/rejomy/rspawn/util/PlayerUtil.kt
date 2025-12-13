package me.rejomy.rspawn.util

import org.bukkit.Location
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import kotlin.math.min

object PlayerUtil {

    fun dropExperience(location: Location, amountOfXP: Int) {
        val experienceOrb = location.world.spawn(location, ExperienceOrb::class.java)
        experienceOrb.experience = min(100, amountOfXP * 7 / 10)
    }

    fun clearEffects(player: Player) {
        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }
    }

    fun resetVariables(player: Player) {
        player.foodLevel = 20
        player.health = player.healthScale
        player.fireTicks = 0
        player.exp = 0F
        player.level = 0
    }
}