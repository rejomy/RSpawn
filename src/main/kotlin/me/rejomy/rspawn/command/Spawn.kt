package me.rejomy.rspawn.command

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.listener.cooldown
import me.rejomy.rspawn.util.TeleportUtil
import me.rejomy.rspawn.util.TitleUtil
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class Spawn : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (args.isEmpty()) {

            if (sender is Player) {

                if (INSTANCE.config.getBoolean("rebirth.block-commands")
                    && cooldown.containsKey(sender.name)
                ) {
                    sender.sendMessage(
                        INSTANCE.config.getString("rebirth.block-commands-message")
                            .replace("&", "§")
                    )
                    return false;
                }

                if (INSTANCE.spawn == null) {
                    sender.sendMessage("Spawn does not set in the config!")
                    sender.sendMessage("Please use /spawn set spawn")
                    return true
                }

                val player: Player = sender
                TeleportUtil.teleportToSpawn(player)
                TitleUtil.displayTitle(player,
                    INSTANCE.config.getString("teleport.title").replace("&", "§"),
                    INSTANCE.config.getString("teleport.subtitle").replace("&", "§"),
                    5, 40, 5
                )

            } else {
                sender.sendMessage("Error! This command can`t executed from console.")
            }
        } else {
            when (args[0]) {
                "set" -> {
                    if (dontHasPermission(sender, "rspawn.command.set",
                            "You dont have permission rspawn.command.set for execute this command.")) {
                        return true
                    }

                    if (args.size != 2) {
                        sender.sendMessage("Illegal arguments, please use /spawn set spawn or /spawn set respawn")
                        return true
                    }

                    if (sender !is Player) {
                        sender.sendMessage("Error! This command can`t executed from console.")
                        return true
                    }

                    val player: Player = sender
                    val location: Location = player.location

                    when (args[1]) {
                        "spawn" -> {
                            savePositionToConfig("spawn", location)
                            INSTANCE.spawn = location

                            sender.sendMessage(
                                "Spawn successful set from location x: " + player.location.x + " y: " + player.location.y +
                                        " z: " + player.location.z
                            )
                        }

                        "respawn" -> {
                            savePositionToConfig("respawn", location)
                            INSTANCE.respawn = location

                            sender.sendMessage(
                                "Respawn successful set from location x: " + player.location.x + " y: " + player.location.y +
                                        " z: " + player.location.z
                            )
                        }

                    }
                }

                "reload" -> {
                    if (dontHasPermission(sender, "rspawn.command.reload",
                            "You dont have permission rspawn.command.reload for execute this command.")) {
                        return true
                    }

                    INSTANCE.reloadConfig()
                    INSTANCE.respawnPriority = INSTANCE.config.getStringList("respawn-priority") as ArrayList<String>
                }

            }
        }

        return true
    }

    fun dontHasPermission(sender: CommandSender, permission: String, message: String): Boolean {
        val dontHasPermission = !sender.hasPermission(permission);

        if (dontHasPermission)
            sender.sendMessage(message)

        return dontHasPermission;
    }
    
    fun savePositionToConfig(path: String, location: Location) {
        val file = File(INSTANCE.dataFolder, File.separator + "location.yml")
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)

        config.set("$path.world", location.world.name)
        config.set("$path.x", location.x)
        config.set("$path.y", location.y)
        config.set("$path.z", location.z)
        config.set("$path.yaw", location.yaw)
        config.set("$path.pitch", location.pitch)
        config.save(file)
    }
}