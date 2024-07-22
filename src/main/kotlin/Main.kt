package ru.airdead.iwseller

import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.airdead.iwseller.command.TestCommand
import ru.airdead.iwseller.data.DatabaseType
import ru.airdead.iwseller.data.RepositoryProvider
import ru.airdead.iwseller.data.profile
import ru.airdead.iwseller.quest.QuestType
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

        runBlocking {
            RepositoryProvider.connect(DatabaseType.MONGODB)
        }

        getCommand("test2")?.setExecutor(TestCommand())

        scheduler.scheduleAtFixedRate({ runBlocking { compareAndUpdateProfiles() } }, 0, 5, TimeUnit.SECONDS)
    }

    override fun onDisable() {
        scheduler.shutdown()
        runBlocking {
            RepositoryProvider.disconnect()
        }
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

    private suspend fun compareAndUpdateProfiles() {
        RepositoryProvider.compareAndUpdateProfiles()
        println("Updated data")
    }
}
