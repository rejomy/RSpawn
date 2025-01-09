package me.rejomy.rspawn.util

import me.rejomy.rspawn.INSTANCE
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

object TeleportUtil {

    fun teleportToSpawn(player: Player) {
        teleport(player, INSTANCE.spawn)
    }

    fun teleportToRespawn(player: Player) {
        teleport(player, INSTANCE.respawn)
    }

    fun teleport(player: Player, location: Location?) {
        if (location == null) {
            throw NullPointerException("Provided location is null.")
        }

        // Safety teleports.
        if (Bukkit.isPrimaryThread()) {
            player.teleport(location)
        } else {
            Bukkit.getScheduler().runTask(INSTANCE) {
                player.teleport(location)
            }
        }
    }
}