package ru.airdead.iwseller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.airdead.iwseller.data.DatabaseManager
import ru.airdead.iwseller.data.playerProfiles
import ru.airdead.iwseller.data.profile
import ru.airdead.iwseller.data.quest.QuestType
import ru.airdead.iwseller.listener.DataManageListener

class Main : JavaPlugin(), Listener {
    val pluginScope = CoroutineScope(Dispatchers.Default)

    override fun onEnable() {
        DatabaseManager.initialize()
        server.pluginManager.registerEvents(DataManageListener(pluginScope), this)
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        runBlocking {
            playerProfiles.values.forEach { profile ->
                DatabaseManager.savePlayerProfile(profile)
            }
        }
        DatabaseManager.closeConnection()
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val killer = entity.killer

        if (killer != null) {
            val profile = killer.profile
            profile.updateQuestProgress(QuestType.Kill(entity.type))
        }
    }
}
