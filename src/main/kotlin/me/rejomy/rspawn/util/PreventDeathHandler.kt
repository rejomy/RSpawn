package me.rejomy.rspawn.util

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.listener.cooldown
import me.rejomy.rspawn.listener.damager
import me.rejomy.rspawn.task.RespawnTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

class PreventDeathHandler(val player: Player, cause: EntityDamageEvent.DamageCause?) {

    private val loc: Location = player.location
    private val name: String = player.name

    init {
        var next = false
        var dname = ""

        if (cause != null) {
            next = true

            val lastPlayerDamager = if (damager.containsKey(player.name)) damager[player.name]!! else "Player"
            dname = if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) lastPlayerDamager else cause.name

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
        } else if (cooldown.containsKey(name)) {
            next = true
        }

        if (next) {

            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "gamemode " + INSTANCE.config.getString("Prevent death.Rebirth.pre-gamemode") + " " + player.name
            )

            if (INSTANCE.config.getBoolean("Prevent death.Rebirth.enable")) {

                val delay: Int

                if (cause != null) {
                    delay = getDelay(player)

                    if (cause == EntityDamageEvent.DamageCause.VOID)
                        player.teleport(INSTANCE.respawn!!.clone().add(0.0, 3.0, 0.0))

                    cooldown[player.name] = delay

                    player.sendTitle(
                        INSTANCE.config.getString("Death.title").replace("&", "§"),
                        INSTANCE.config.getString("Death.subtitle").replace("\$killer", dname).replace("&", "§")
                    )
                } else {
                    delay = cooldown[name]!!
                }

                val task = RespawnTask(delay, player);

                val taskRun = Bukkit.getScheduler().runTaskTimer(INSTANCE, { task.run() }, 20L, 20L)

                task.task = taskRun
            } else {
                player.teleport(INSTANCE.respawn)
            }

            if (cause != null) {
                PlayerUtil.clearEffects(player);

                // set statistic
                player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1)

                if (damager[player.name] != null && Bukkit.getPlayer(damager[player.name]) != null)
                    Bukkit.getPlayer(damager[player.name]!!)
                        .setStatistic(
                            Statistic.PLAYER_KILLS,
                            Bukkit.getPlayer(damager[player.name]!!).getStatistic(Statistic.PLAYER_KILLS) + 1
                        )
            }

            PlayerUtil.resetVariables(player);
        }
    }

}