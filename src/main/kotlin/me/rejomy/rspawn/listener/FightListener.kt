package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.ar
import me.rejomy.rspawn.duel
import me.rejomy.rspawn.util.getDelay
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack

val cooldown: MutableMap<String, Int> = mutableMapOf()
val notOnline = ArrayList<Player>()

class FightListener : Listener {

    private val damager: MutableMap<String, String> = mutableMapOf()

    @EventHandler(priority = EventPriority.MONITOR)
    fun onDamage(event: EntityDamageEvent) {
        if (event.isCancelled || event.entity !is Player || !INSTANCE.config.getBoolean("Prevent death.enable")) return

        // Links
        val player: Player = event.entity as Player
        val world = player.world.name
        if(duel != null && duel!!.arenaManager.isInMatch(
                player
            )) return
        for(bw in INSTANCE.disableWorlds) {
            if(bw == world) return
        }

        val loc = player.location

        if (player.location.y < 0 && INSTANCE.config.getBoolean("Teleport.fall") && !ar.pvpManager.isInPvP(player)) {
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

        // Links 2
        val dpre: String = if (damager.containsKey(player.name)) damager[player.name]!! else "Player"
        val dname: String = if (event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) dpre else event.cause.name


        val drops = player.inventory.contents.toMutableList()
        drops.addAll(player.inventory.armorContents.toList())
        drops.add(player.itemOnCursor)
        drops.removeIf { it == null || it.type == Material.AIR }

        // call death event
        Bukkit.getPluginManager().callEvent(
            PlayerDeathEvent(
                player,
                drops, player.expToLevel, "Player has been killed $dname"
            )
        )

        drops.forEach { loc.world.dropItemNaturally(loc, it) }

        player.inventory.clear()
        player.itemOnCursor = null
        player.inventory.armorContents =
            arrayOf(
                ItemStack(Material.AIR),
                ItemStack(Material.AIR),
                ItemStack(Material.AIR),
                ItemStack(Material.AIR)
            )
        if (!Bukkit.getVersion().contains("1.8")) {
            player.inventory.itemInOffHand = null
        }

        //damage effect
        player.damage(0.0)

        if (INSTANCE.config.getBoolean("Prevent death.Rebirth.enable")) {

            if (event.cause == EntityDamageEvent.DamageCause.VOID) player.teleport(
                INSTANCE.respawn!!.clone().add(0.0, 3.0, 0.0)
            )
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "gamemode " + INSTANCE.config.getString("Prevent death.Rebirth.pre-gamemode") + " " + player.name
            )
            val taskID: Int
            var delay = getDelay(player)

            cooldown[player.name] = delay

            player.sendTitle(
                INSTANCE.config.getString("Death.title").replace("&", "§"),
                INSTANCE.config.getString("Death.subtitle").replace("\$killer", dname).replace("&", "§")
            )

            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, {
                if (player.isOnline && cooldown.containsKey(player.name) || !notOnline.contains(player)) {
                    delay -= 1

                    cooldown[player.name] = delay

                    if (delay < 0) Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "ban Regomy Опять фигню сделал! Delay $delay & Map ${cooldown[player.name]}"
                    )

                    player.sendTitle(
                        INSTANCE.config.getString("Prevent death.Rebirth.Delay.title").replace("&", "§"),
                        INSTANCE.config.getString("Prevent death.Rebirth.Delay.subtitle")
                            .replace("\$delay", "$delay")
                            .replace("&", "§")
                    )
                } else if (!player.isOnline)
                    notOnline.add(player)
            }, 20, 20)

            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, {
                Bukkit.getScheduler().cancelTask(taskID)
                if (player.isOnline) {
                    if (!leave.contains(player.name)) {
                        player.sendTitle(
                            INSTANCE.config.getString("Prevent death.Rebirth.title").replace("&", "§"),
                            INSTANCE.config.getString("Prevent death.Rebirth.subtitle").replace("&", "§")
                        )
                        if (ar.pvpManager.isInPvP(player))
                            ar.pvpManager.stopPvP(player)

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
                    } else
                        leave.remove(player.name)
                }
            }, (delay * 20).toLong())
        } else
            player.teleport(INSTANCE.respawn)

        // set statistic
        for (effect in player.activePotionEffects) player.removePotionEffect(effect.type)
        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1)
        if (damager[player.name] != null && Bukkit.getPlayer(damager[player.name]) != null)
            Bukkit.getPlayer(damager[player.name]!!)
            .setStatistic(
                Statistic.PLAYER_KILLS,
                Bukkit.getPlayer(damager[player.name]!!).getStatistic(Statistic.PLAYER_KILLS) + 1
            )
        player.foodLevel = 20
        player.health = player.maxHealth
        player.exp = 0F

        event.isCancelled = true
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player) return
        var killer = event.damager
        val world = killer.world.name
        if(INSTANCE.disableWorlds.contains(world)) return

        if (killer is Projectile && (killer.shooter is Monster || killer.shooter is Player))
            killer = killer.shooter as Entity
        damager[event.entity.name] = killer.name
    }

}