package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val blockCommands = INSTANCE.config.getBoolean("rebirth.block-commands");
        val respawning = cooldown.containsKey(player.name);

        if (blockCommands && respawning) {
            player.sendMessage(
                INSTANCE.config.getString("rebirth.block-commands-message").replace("&", "§")
            )
            event.isCancelled = true
        }
    }
}