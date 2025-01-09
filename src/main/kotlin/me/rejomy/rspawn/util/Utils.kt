package me.rejomy.rspawn.util

import me.rejomy.rspawn.INSTANCE
import org.bukkit.Location
import org.bukkit.entity.Player

object Utils {

    fun getRespawnDelay(player: Player): Int {
        for (string in INSTANCE.delay) {
            val parts = string.split("\\s+".toRegex())

            if (player.hasPermission(parts[0])) {
                return parts[1].toInt()
            }
        }

        return INSTANCE.defaultDelay
    }

    fun getRespawnLocation(player: Player): Location? {
        for (priority in INSTANCE.respawnPriority) {
            when (priority) {
                "bed" ->
                    if (player.bedSpawnLocation != null) {
                        return player.bedSpawnLocation
                    }

                "spawn" ->
                    if (INSTANCE.respawn != null) {
                        return INSTANCE.respawn
                    }
            }
        }

        println("RSpawn -> Respawn priority incorrect!")
        return null
    }

    fun teleportToRespawn(player: Player) {
        val location = getRespawnLocation(player)

        if (location != null) {
            TeleportUtil.teleport(player, location)
        }
    }
}
