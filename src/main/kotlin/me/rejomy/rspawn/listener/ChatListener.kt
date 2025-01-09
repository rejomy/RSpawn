package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.antirelog
import me.rejomy.rspawn.duel
import me.rejomy.rspawn.util.TeleportUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncPlayerChatEvent) {
        if (!INSTANCE.config.getBoolean("chat")) {
            return
        }

        val player = event.player
        val world = player.world.name

        val isInDuel = duel != null && duel!!.arenaManager.isInMatch(player)
        val isInDisabledWorld = INSTANCE.disableWorlds.any { it == world }
        val isInPvP = antirelog != null && antirelog!!.pvpManager.isInPvP(player);
        // If commands while player respawning is blocked, dont teleport him to the spawn.
        val isRespawning = cooldown.containsKey(player.name) && INSTANCE.config.getBoolean("rebirth.block-commands");

        if (isInDisabledWorld || isInDuel || isRespawning || isInPvP) {
            return
        }

        if (arrayOf("spawn", "spaw", "spwn", "spw", "sawn", "pawn", "ызфцт", "ызфц")
            .contains(removeSpecialCharacter(event.message))) {
            event.isCancelled = true
            TeleportUtil.teleportToSpawn(player)
        }
    }

    private fun removeSpecialCharacter(message: String): String {
        if (message.isNotEmpty()) {
            val firstChar = message[0]

            if (firstChar == ',' || firstChar == '.' || firstChar == '/' || firstChar == '\\' || firstChar == '?') {
                return message.substring(1)
            }
        }

        return message
    }

}