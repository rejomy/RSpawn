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
var antirelog: Antirelog? = null
var duel: Duels? = null

class Main : JavaPlugin() {

    val disableWorlds = ArrayList<String>()
    var delay = config.getStringList("rebirth.delay.permissions")
    var defaultDelay = config.getInt("rebirth.delay.default")
    var spawn: Location? = null
    var respawn: Location? = null
    var respawnPriority = ArrayList<String>()

    override fun onLoad() {
        INSTANCE = this;

        // Save default config for create config file and folder if it does not exists.
        saveDefaultConfig()
    }

    override fun onEnable() {
        duel = Bukkit.getServer().pluginManager.getPlugin("Duels") as Duels?
        antirelog = Bukkit.getServer().pluginManager.getPlugin("AntiRelog") as Antirelog?

        val file = File(dataFolder, "location.yml")

        if (file.exists()) {
            val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
            respawn = getLocationFromFile("respawn", config)
            spawn = getLocationFromFile("spawn", config)
        } else {
            for (world in Bukkit.getWorlds()) {
                spawn = world.spawnLocation
                break
            }
        }

        respawnPriority = config.getStringList("respawn-priority") as ArrayList<String>

        for (world in config.getStringList("disable-worlds")) {
            disableWorlds.add(world)
        }

        // Register listeners
        arrayOf(
            ConnectionListener(),
            DeathListener(),
            RespawnListener(),
            FightListener(),
            CommandListener(),
            ChatListener(),
            InteractListener(),
            TeleportListener(),
        ).forEach {
            Bukkit.getPluginManager().registerEvents(it, this)
        }

        getCommand("spawn").executor = Spawn()
        if (config.getBoolean("register-kill-command"))
            getCommand("kill").executor = KillPlayer()
    }

    private fun getLocationFromFile(path: String, config: FileConfiguration): Location {
        return Location(
            Bukkit.getWorld(config.getString("$path.world")),
            config.getDouble("$path.x"),
            config.getDouble("$path.y"),
            config.getDouble("$path.z"),
            config.getDouble("$path.yaw").toFloat(),
            config.getDouble("$path.pitch").toFloat()
        )
    }
}
