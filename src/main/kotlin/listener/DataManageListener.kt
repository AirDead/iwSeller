package ru.airdead.iwseller.listener

import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.airdead.iwseller.data.MongoService
import ru.airdead.iwseller.data.PlayerProfile
import ru.airdead.iwseller.data.playerProfiles
import ru.airdead.iwseller.data.profile

class DataManageListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) = runBlocking {
        playerProfiles[event.player.name] = MongoService.getPlayerProfile(event.player.name) ?: PlayerProfile(event.player.name, 1, 0)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) = runBlocking {
        MongoService.savePlayerProfile(event.player.profile)
        playerProfiles.remove(event.player.name)
    }
}