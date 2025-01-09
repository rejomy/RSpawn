package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.duel
import me.rejomy.rspawn.util.PreventDeathHandler
import me.rejomy.rspawn.util.Utils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class RespawnListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val world = player.world.name
        val isInDuel = duel != null && duel!!.arenaManager.isInMatch(player)
        val isInDisabledWorld = INSTANCE.disableWorlds.any { it == world }
        val preventDeath = INSTANCE.config.getBoolean("prevent-death.enable");
        val respawnCalledByUs = cooldown.containsKey(player.name) // Be sure that player is not respawning on this time.

        if (isInDisabledWorld || isInDuel || respawnCalledByUs) {
            return
        }

        if (INSTANCE.config.getBoolean("teleport.respawn")) {
            val location = Utils.getRespawnLocation(player)

            if (location != null) {
                event.respawnLocation = location
            }
        }

        // Run respawn timer after respawn.
        if (preventDeath) {
            PreventDeathHandler(player, player.lastDamageCause.cause)
        }
    }
}
