package ru.airdead.iwseller.listener

import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.airdead.iwseller.data.PlayerProfile
import ru.airdead.iwseller.data.RepositoryProvider
import ru.airdead.iwseller.data.playerProfiles
import ru.airdead.iwseller.data.profile

class DataManageListener : Listener {
    @EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        runBlocking {
            val player = event.player
            val profile = RepositoryProvider.getPlayerProfile(player.name)
            playerProfiles[player.name] = profile ?: PlayerProfile(player.name, 1, 0)
            println("Loaded data for ${player.name}")
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        runBlocking {
            val player = event.player
            RepositoryProvider.savePlayerProfile(player.profile)
            playerProfiles.remove(player.name)
            println("Saved data for ${player.name}")
        }
    }
}
