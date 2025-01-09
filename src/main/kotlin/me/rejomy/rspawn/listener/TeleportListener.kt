package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class TeleportListener: Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val preventTeleportToOtherPlayersInSpectator = INSTANCE.config.getBoolean("rebirth.prevent-spec");
        val teleportCauseIsSpectate = event.cause == PlayerTeleportEvent.TeleportCause.SPECTATE;
        val preventPosses = INSTANCE.config.getBoolean("rebirth.prevent-posses-players-in-spectator") &&
                event.cause == PlayerTeleportEvent.TeleportCause.UNKNOWN
        val respawning = cooldown.containsKey(event.player.name);

        if (preventTeleportToOtherPlayersInSpectator && (teleportCauseIsSpectate || preventPosses) && respawning) {
            event.isCancelled = true

            // Prevent players join to the other players while respawning
            if (preventPosses && player.gameMode == GameMode.SPECTATOR)
                player.spectatorTarget = null
        }
    }
}