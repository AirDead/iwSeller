package ru.airdead.iwseller

import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.airdead.iwseller.data.MongoService
import ru.airdead.iwseller.data.playerProfiles
import ru.airdead.iwseller.data.profile
import ru.airdead.iwseller.data.quest.QuestType
import ru.airdead.iwseller.listener.DataManageListener
import ru.airdead.iwseller.listener.MenuListener
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Main : JavaPlugin(), Listener {
    private val scheduler = Executors.newScheduledThreadPool(1)

    override fun onEnable() {
        server.pluginManager.registerEvents(DataManageListener(), this)
        server.pluginManager.registerEvents(this, this)
        server.pluginManager.registerEvents(MenuListener, this)


        scheduler.scheduleAtFixedRate(::compareAndUpdateProfiles, 0, 5, TimeUnit.MINUTES)
    }

    override fun onDisable() {
        scheduler.shutdown()
        MongoService.mongoClient.close()
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

    private fun compareAndUpdateProfiles() = runBlocking {
        val profilesFromDB = MongoService.playersMap
        profilesFromDB.forEach { dbProfile ->
            val cachedProfile = playerProfiles[dbProfile.key]
            if (cachedProfile != null && cachedProfile != dbProfile.value) {
                MongoService.savePlayerProfile(cachedProfile)
            }
        }
    }

}