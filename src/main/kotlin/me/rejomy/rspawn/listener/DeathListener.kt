package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.duel
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathListener : Listener {

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity.player
        val world = player.world.name
        val isInDuel = duel != null && duel!!.arenaManager.isInMatch(player)
        val isInDisabledWorld = INSTANCE.disableWorlds.any { it == world }
        val playerIsNotDied = player.isDead // Be sure cuz our plugin send player death event too, but player alive.

        if (isInDisabledWorld || isInDuel || playerIsNotDied) {
            return
        }

        if (INSTANCE.config.getBoolean("death.auto-respawn")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, {
                player.spigot().respawn()
            }, 1)
        }
    }
}