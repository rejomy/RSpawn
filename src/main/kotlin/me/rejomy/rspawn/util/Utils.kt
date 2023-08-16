package me.rejomy.rspawn.util

import me.rejomy.rspawn.INSTANCE
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun getDelay(player: Player): Int {
    for (amount in INSTANCE.delay) {
        val line = amount.split(" ")
        if (player.hasPermission(line[0]))
            return line[1].toInt()
    }
    return INSTANCE.defaultDelay
}

fun respawnPlayer(player: Player) {

    var status = false

    for(priority in INSTANCE.respawnPriority) {
        when(priority) {
            "bed" ->
                if(player.bedSpawnLocation != null) {
                    player.teleport(player.bedSpawnLocation)
                    status = true
                    break
                }
            "spawn" ->
                if(INSTANCE.respawn != null) {
                    status = true
                    player.teleport(INSTANCE.respawn)
                    break
                }
        }
        if(!status) {
            println("RSpawn -> Respawn priority incorrect!")
        }
    }

}
