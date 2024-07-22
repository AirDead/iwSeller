package ru.airdead.iwseller.data

import org.bukkit.Bukkit
import ru.airdead.iwseller.data.memory.InMemoryPlayerProfileRepository
import ru.airdead.iwseller.data.mongodb.MongoPlayerProfileRepository

object RepositoryProvider {
    private val logger = Bukkit.getLogger()
    lateinit var databaseType: DatabaseType
    private var playerProfileRepository: PlayerProfileRepository? = null

    suspend fun connect(dataType: DatabaseType) {
        databaseType = dataType
        playerProfileRepository = when (databaseType) {
            DatabaseType.IN_MEMORY -> InMemoryPlayerProfileRepository()
            DatabaseType.MONGODB -> MongoPlayerProfileRepository()
        }
        playerProfileRepository?.connect()
        logger.info("[IwSeller] Connected to ${databaseType.displayName}")
    }

    suspend fun disconnect() {
        playerProfileRepository?.disconnect()
        playerProfileRepository = null
        logger.info("[IwSeller] Disconnected from ${databaseType.displayName}")
    }

    suspend fun savePlayerProfile(profile: PlayerProfile) {
        playerProfileRepository?.savePlayerProfile(profile)
    }

    suspend fun getPlayerProfile(name: String): PlayerProfile? {
        return playerProfileRepository?.getPlayerProfile(name)
    }

    suspend fun compareAndUpdateProfiles() {
        playerProfileRepository?.compareAndUpdateProfiles()
    }
}
