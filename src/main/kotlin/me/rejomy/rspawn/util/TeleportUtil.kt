package me.rejomy.rspawn.util

import me.rejomy.rspawn.INSTANCE
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

        player.teleportAsync(location)
    }
}