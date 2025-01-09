package me.rejomy.rspawn.task

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.antirelog
import me.rejomy.rspawn.listener.cooldown
import me.rejomy.rspawn.util.PlayerUtil
import me.rejomy.rspawn.util.TitleUtil
import me.rejomy.rspawn.util.Utils
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
        // If player not in the server for any reason, stop scheduler.
        // We will run it again if he join back with his cached cooldown.
        if (!player.isOnline) {
            task.cancel()
        } else if (--delay < 1) {
            TitleUtil.displayTitle(player,
                INSTANCE.config.getString("rebirth.title").replace("&", "§"),
                INSTANCE.config.getString("rebirth.subtitle").replace("&", "§"),
                10, 30, 10
            )

            if (antirelog != null && antirelog!!.pvpManager.isInPvP(player)) {
                antirelog!!.pvpManager.stopPvP(player)
            }

            Bukkit.getPluginManager().callEvent(PlayerRespawnEvent(player, INSTANCE.respawn, false))

            Utils.teleportToRespawn(player)

            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "gamemode " + INSTANCE.config.getString("rebirth.post-gamemode") + " " + player.name
            )

            PlayerUtil.resetVariables(player)
            cooldown.remove(player.name)
            task.cancel()
        } else {
            cooldown[player.name] = delay

            TitleUtil.displayTitle(player,
                INSTANCE.config.getString("rebirth.delay.title").replace("&", "§"),
                INSTANCE.config.getString("rebirth.delay.subtitle")
                    .replace("\$delay", "$delay")
                    .replace("&", "§"),
                3, 40, 3
            )
        }
    }

}