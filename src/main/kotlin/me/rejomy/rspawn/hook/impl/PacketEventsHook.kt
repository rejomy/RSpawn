package me.rejomy.rspawn.hook.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.PacketEventsAPI

object PacketEventsHook {

    var enable = false
    lateinit var packetEventsAPI: PacketEventsAPI<*>

    init {
        try {
            packetEventsAPI = PacketEvents.getAPI()
            enable = true // Assuming you want to set enable to true if initialization succeeds
        } catch (exception: Exception) {
            enable = false;
        }
    }
}
