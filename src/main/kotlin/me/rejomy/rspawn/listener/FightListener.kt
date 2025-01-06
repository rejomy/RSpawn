package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.ar
import me.rejomy.rspawn.duel
import me.rejomy.rspawn.util.PreventDeathHandler
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

val cooldown: MutableMap<String, Int> = mutableMapOf()
val damager: MutableMap<String, String> = mutableMapOf()

class FightListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onDamage(event: EntityDamageEvent) {
        if (event.isCancelled || event.entity !is Player || !INSTANCE.config.getBoolean("Prevent death.enable")) return

        // Links
        val player: Player = event.entity as Player
        val world = player.world.name

        if(duel != null && duel!!.arenaManager.isInMatch(player)) return

        INSTANCE.disableWorlds.forEach {
            if(it == world) return
        }

        val loc = player.location

        if (loc.y < 0 && INSTANCE.config.getBoolean("Teleport.fall")
            && !(ar != null && ar!!.pvpManager.isInPvP(player))) {
            event.isCancelled = true
            player.teleport(INSTANCE.spawn)
            return
        }

        if (player.gameMode == GameMode.SPECTATOR) {
            event.isCancelled = true
            return
        }

        // check if player health < damage
        if (player.health > event.finalDamage) return

        PreventDeathHandler(player, event.cause)

        event.isCancelled = true
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player) return

        var killer = event.damager
        val world = killer.world.name

        if (INSTANCE.disableWorlds.contains(world))
            return

        if (killer is Projectile && (killer.shooter is Monster || killer.shooter is Player))
            killer = killer.shooter as Entity

        damager[event.entity.name] = killer.name
    }

}