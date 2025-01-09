package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.antirelog
import me.rejomy.rspawn.duel
import me.rejomy.rspawn.util.PreventDeathHandler
import me.rejomy.rspawn.util.ServerVersionUtil
import me.rejomy.rspawn.util.TeleportUtil
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player || !INSTANCE.config.getBoolean("prevent-death.enable")) {
            return
        }

        val player = event.entity as Player
        val location = player.location

        val isInDuel = duel != null && duel!!.arenaManager.isInMatch(player)
        val isInDisabledWorld = INSTANCE.disableWorlds.any { it == player.world.name }
        val isInPvP = antirelog != null && antirelog!!.pvpManager.isInPvP(player)
        val respawning = cooldown.containsKey(player.name) // Prevent to damage peoples who are respawning.
        val teleportIfFall = INSTANCE.config.getBoolean("teleport.fall") && !isInPvP

        if (isInDisabledWorld || isInDuel) {
            return
        }

        // Handle fall check
        if (teleportIfFall) {
            // Versions before 1.17 does not have negative height.
            var minYHeight = 0

            if (ServerVersionUtil.newerThanOrEquals(117)) {
                minYHeight = -65
            }

            if (location.y < minYHeight) {
                TeleportUtil.teleportToSpawn(player)
                event.isCancelled = true
                return
            }
        }

        if (respawning) {
            event.isCancelled = true
            return
        }

        // If player didnt survive this hit, we cancel hit and handle player die in our side.
        if (player.health <= event.finalDamage) {
            PreventDeathHandler(player, event.cause)
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player) return

        var killer = event.damager
        val world = killer.world.name

        if (INSTANCE.disableWorlds.contains(world))
            return

        if (killer is Projectile && (killer.shooter is Monster || killer.shooter is Player)) {
            // If attacker is arrow, we get arrow shooter and set shooter as attacker.
            killer = killer.shooter as Entity
        }

        damager[event.entity.name] = killer.name
    }

}