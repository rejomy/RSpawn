package me.rejomy.rspawn.util

import org.bukkit.Bukkit

object ServerVersionUtil {

    private val serverVersion: Int = getServerVersion()

    fun newerThan113(): Boolean {
        return serverVersion > 113
    }

    fun newerThan(version: Int): Boolean {
        return serverVersion > version
    }

    fun newerThanOrEquals(version: Int): Boolean {
        return serverVersion >= version
    }

    fun newerThan18(): Boolean {
        return serverVersion > 18
    }

    private fun getServerVersion(): Int {
        // Get the full version string from the server
        val version = Bukkit.getBukkitVersion()

        // Extract the version number (e.g., "1.8" or "1.13")
        // The problem we cant take 1.8.8, it will 188, so we get only two first elements.
        //  and we are dont know actual version last number. 1.21, 1.16, but not 1.16.5.
        val versionNumbers = version.split("-")[0].take(2).split(".")

        // Combine the major and minor version parts (e.g., "1.8" -> "18", "1.13" -> "113")
        val formattedVersion = versionNumbers.joinToString("") { it }.toInt()

        // Return the version as an integer
        return formattedVersion
    }
}
