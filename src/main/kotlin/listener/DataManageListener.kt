package ru.airdead.iwseller.listener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.airdead.iwseller.data.DatabaseManager
import ru.airdead.iwseller.data.PlayerProfile
import ru.airdead.iwseller.data.playerProfiles
import ru.airdead.iwseller.data.profile

class DataManageListener(
    val pluginScope: CoroutineScope
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        pluginScope.launch {
            val profile = DatabaseManager.loadPlayerProfile(player.name)
            if (profile != null) {
                playerProfiles[player.name] = profile
            } else {
                playerProfiles[player.name] = PlayerProfile(player.name, 1, 0)
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val profile = player.profile
        pluginScope.launch {
            DatabaseManager.savePlayerProfile(profile)
            playerProfiles.remove(player.name)
        }
    }
}
