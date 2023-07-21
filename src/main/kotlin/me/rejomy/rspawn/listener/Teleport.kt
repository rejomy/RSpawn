package me.rejomy.rspawn.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class Teleport: Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onTeleport(event: PlayerTeleportEvent) {
        if(event.cause == PlayerTeleportEvent.TeleportCause.SPECTATE && cooldown.containsKey(event.player.name))
            event.isCancelled = true
    }

}