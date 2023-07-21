package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

val leave = ArrayList<String>()

class ConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player

        if (INSTANCE.config.getBoolean("Teleport.join"))
            player.teleport(INSTANCE.spawn)

        val name = player.name

        if(cooldown.containsKey(name)) {

            player.gameMode = GameMode.SPECTATOR

            val taskID: Int = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, {
                if(player.isOnline && notOnline.contains(player)) {

                    cooldown[player.name] = cooldown[player.name]!! - 1

                    if (cooldown[player.name]!! < 0) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban Regomy Опять фигню сделал!")

                    player.sendTitle(
                        INSTANCE.config.getString("Prevent death.Rebirth.Delay.title").replace("&", "§"),
                        INSTANCE.config.getString("Prevent death.Rebirth.Delay.subtitle").replace("\$delay", "${cooldown[player.name]}")
                            .replace("&", "§")
                    )
                }
            }, 40, 20)

            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, {
                if(player.isOnline && leave.contains(player.name)) {
                    player.sendTitle(
                        INSTANCE.config.getString("Prevent death.Rebirth.title").replace("&", "§"),
                        INSTANCE.config.getString("Prevent death.Rebirth.subtitle").replace("&", "§")
                    )

                    Bukkit.getPluginManager().callEvent(
                        PlayerRespawnEvent(
                            player, INSTANCE.respawn, false
                        )
                    )
                    player.exp = 0F

                    player.teleport(INSTANCE.respawn)

                    Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "gamemode " + INSTANCE.config.getString("Prevent death.Rebirth.post-gamemode") + " " + player.name
                    )
                    cooldown.remove(player.name)
                    notOnline.remove(player)
                    if(leave.contains(player.name))
                        leave.remove(player.name)
                } else
                    leave.add(player.name)
                Bukkit.getScheduler().cancelTask(taskID)
            }, (cooldown[player.name]!! * 20).toLong())
        }

    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player: Player = event.player
        if(leave.contains(player.name)) {
            leave.remove(player.name)
        } else if(cooldown.containsKey(player.name))
            leave.add(player.name)
    }
}