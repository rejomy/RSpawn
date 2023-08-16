package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.util.respawnPlayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent

class DeathListener : Listener {

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity.player

        val world = player.world.name
        if (INSTANCE.disableWorlds.contains(world)) return

        if (INSTANCE.config.getBoolean("Death.auto respawn")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, {
                player.spigot().respawn()
                respawnPlayer(player)
            }, 4)
        }

    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val world = player.world.name

        if (INSTANCE.disableWorlds.contains(world)) return

        if (INSTANCE.config.getBoolean("Teleport.death")) {
            respawnPlayer(player)
        }
    }

}