package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandListener : Listener {

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        if(cooldown.containsKey(player.name)) {
            player.sendMessage(INSTANCE.config.getString("Prevent death.Rebirth.block-commands-message")
                .replace("&", "§"))
            event.isCancelled = true
        }

    }

}