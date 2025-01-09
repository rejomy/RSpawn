package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.util.PreventDeathHandler
import me.rejomy.rspawn.util.TeleportUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player

        if (player.hasPlayedBefore()) {
            if (INSTANCE.config.getBoolean("teleport.join.after-first")) {
                TeleportUtil.teleportToSpawn(player)
            }
        } else if (INSTANCE.config.getBoolean("teleport.join.first")) {
            Bukkit.getScheduler().runTaskLater(INSTANCE, { player.teleport(INSTANCE.spawn) }, 2)
        }

        // Run check that player before was killed and his cool-down is not elapsed.
        PreventDeathHandler(player, null)
    }

}