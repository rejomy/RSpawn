package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent

class Chat : Listener {

    fun removeSpecialCharacter(message: String): String {
        if (message.isNotEmpty()) {
            val firstChar = message[0]
            if (firstChar == ',' || firstChar == '.' || firstChar == '/' || firstChar == '\\' || firstChar == '?') {
                return message.substring(1)
            }
        }
        return message
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: PlayerChatEvent) {
        val player = event.player
        val world = player.world.name
        if(INSTANCE.disableWorlds.contains(world)) return
        if(arrayOf("spawn", "spaw", "spwn", "spw", "sawn", "pawn", "ызфцт", "ызфц").contains(removeSpecialCharacter(event.message))) {
            event.isCancelled = true
            player.teleport(INSTANCE.spawn)
        }

    }

}