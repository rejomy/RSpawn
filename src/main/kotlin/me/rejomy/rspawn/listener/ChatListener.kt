package me.rejomy.rspawn.listener

import io.papermc.paper.event.player.AsyncChatEvent
import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.antirelog
import me.rejomy.rspawn.duel
import me.rejomy.rspawn.util.TeleportUtil
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncChatEvent) {
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

        if (arrayOf("spawn", "spaw", "spwn", "spw", "sawn", "pawn", "spanw", "ызфцт", "ызфц")
            .contains(
                removeSpecialCharacter(PlainTextComponentSerializer.plainText().serialize(event.message())))) {
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