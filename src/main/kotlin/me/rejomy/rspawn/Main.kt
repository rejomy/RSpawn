package me.rejomy.rspawn

import me.realized.duels.api.Duels
import me.rejomy.rspawn.command.KillPlayer
import me.rejomy.rspawn.command.Spawn
import me.rejomy.rspawn.listener.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import ru.leymooo.antirelog.Antirelog
import java.io.File

lateinit var INSTANCE: Main
var ar: Antirelog? = null
var duel: Duels? = null

class Main : JavaPlugin() {

    val disableWorlds = ArrayList<String>()

    var delay = config.getStringList("Prevent death.Rebirth.Delay.permissions")
    var defaultDelay = config.getInt("Prevent death.Rebirth.Delay.default")
    var spawn: Location? = null
    var respawn: Location? = null
    var respawnPriority = ArrayList<String>()

    override fun onEnable() {
        INSTANCE = this
        saveDefaultConfig()

        duel = Bukkit.getServer().pluginManager.getPlugin("Duels") as Duels?
        ar = Bukkit.getServer().pluginManager.getPlugin("AntiRelog") as Antirelog?

        val file = File(dataFolder, "location.yml")
        if (file.exists()) {
            val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
            spawn = Location(
                Bukkit.getWorld(config.getString("Spawn.world")),
                config.getDouble("Spawn.x"),
                config.getDouble("Spawn.y"),
                config.getDouble("Spawn.z"),
                config.getDouble("Spawn.yaw").toFloat(),
                config.getDouble("Spawn.pitch").toFloat()
            )

            respawn = Location(
                Bukkit.getWorld(config.getString("Respawn.world")),
                config.getDouble("Respawn.x"),
                config.getDouble("Respawn.y"),
                config.getDouble("Respawn.z"),
                config.getDouble("Respawn.yaw").toFloat(),
                config.getDouble("Respawn.pitch").toFloat()
            )
        }

        respawnPriority = config.getStringList("respawn priority") as ArrayList<String>

        for(world in config.getStringList("disable worlds")) {
            disableWorlds.add(world)
        }

        Bukkit.getPluginManager().registerEvents(ConnectionListener(), this)
        Bukkit.getPluginManager().registerEvents(DeathListener(), this)
        Bukkit.getPluginManager().registerEvents(FightListener(), this)

        if(config.getBoolean("Prevent death.Rebirth.block-commands")) {
            Bukkit.getPluginManager().registerEvents(CommandListener(), this)
        }

        if (config.getBoolean("chat"))
            Bukkit.getPluginManager().registerEvents(Chat(), this)
        if (config.getBoolean("Prevent death.Rebirth.prevent_spec"))
            Bukkit.getPluginManager().registerEvents(Teleport(), this)

        getCommand("spawn").executor = Spawn()

        if(config.getBoolean("Prevent death.enable"))
            getCommand("kill").executor = KillPlayer()

    }

}
