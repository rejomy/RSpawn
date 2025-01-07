package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.util.PreventDeathHandler
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
            if (INSTANCE.config.getBoolean("Teleport.join.after first")) {
                player.teleport(INSTANCE.spawn)
            }
        } else if (INSTANCE.config.getBoolean("Teleport.join.first")) {
            Bukkit.getScheduler().runTaskLater(INSTANCE, { player.teleport(INSTANCE.spawn) }, 3)
        }

        PreventDeathHandler(player, null)
    }

}