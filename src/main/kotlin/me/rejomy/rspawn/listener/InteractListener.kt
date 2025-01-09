package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class InteractListener: Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val isInDisabledWorld = INSTANCE.disableWorlds.any { it == player.world.name }
        val respawning = cooldown.containsKey(player.name); // Prevent to damage peoples who are respawning.
        val cancelInteractInSpectator = INSTANCE.config.getBoolean("rebirth.prevent-interact")

        if (!isInDisabledWorld && respawning && cancelInteractInSpectator) {
            event.isCancelled = true
        }
    }
}