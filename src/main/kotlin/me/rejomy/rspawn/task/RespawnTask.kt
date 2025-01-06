package me.rejomy.rspawn.task

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.ar
import me.rejomy.rspawn.listener.cooldown
import me.rejomy.rspawn.util.respawnPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.scheduler.BukkitTask

class RespawnTask(
    private var delay: Int,
    val player: Player,
) {
    lateinit var task: BukkitTask
    fun run() {
        if (!player.isOnline) {
            task.cancel()
        } else if(delay < 1) {
            player.sendTitle(
                INSTANCE.config.getString("Prevent death.Rebirth.title").replace("&", "§"),
                INSTANCE.config.getString("Prevent death.Rebirth.subtitle").replace("&", "§")
            )

            if (ar != null && ar!!.pvpManager.isInPvP(player)) {
                ar!!.pvpManager.stopPvP(player)
            }

            Bukkit.getPluginManager().callEvent(PlayerRespawnEvent(player, INSTANCE.respawn, false))

            player.exp = 0F

            respawnPlayer(player)

            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "gamemode " + INSTANCE.config.getString("Prevent death.Rebirth.post-gamemode") + " " + player.name
            )

            cooldown.remove(player.name)

            task.cancel()
        } else {
            delay -= 1

            cooldown[player.name] = delay

            player.sendTitle(
                INSTANCE.config.getString("Prevent death.Rebirth.Delay.title").replace("&", "§"),
                INSTANCE.config.getString("Prevent death.Rebirth.Delay.subtitle")
                    .replace("\$delay", "$delay")
                    .replace("&", "§")
            )
        }
    }
}