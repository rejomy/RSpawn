package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class PickupListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPickup(event: EntityPickupItemEvent) {
        val entity = event.entity
        val respawning = cooldown.containsKey(entity.name)
        val isInDisabledWorld = INSTANCE.disableWorlds.any { it == entity.world.name }
        val preventPickup = INSTANCE.config.getBoolean("rebirth.cancel-pickup-during-respawning")

        if (respawning && !isInDisabledWorld && preventPickup)
            event.isCancelled = true
    }
}