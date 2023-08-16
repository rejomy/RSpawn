package me.rejomy.rspawn.command

import me.rejomy.rspawn.util.PreventDeathHandler
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.entity.EntityDamageEvent

class KillPlayer : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (args.isEmpty()) {
            sender.sendMessage("Args is empty.")
            return false
        }

        val player = Bukkit.getPlayer(args[0])

        if (player == null) {
            sender.sendMessage("Player not found.")
            return false
        }

        if (player.isDead) {
            sender.sendMessage("Plugin can`t kill the player, the player is dead.")
        } else {
            PreventDeathHandler(player, EntityDamageEvent.DamageCause.CUSTOM)
        }

        return false
    }
}
