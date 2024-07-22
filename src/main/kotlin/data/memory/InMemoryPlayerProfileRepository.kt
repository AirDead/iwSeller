package ru.airdead.iwseller.data.memory

import ru.airdead.iwseller.data.PlayerProfile
import ru.airdead.iwseller.data.PlayerProfileRepository
import ru.airdead.iwseller.data.playerProfiles
import java.util.concurrent.ConcurrentHashMap

class InMemoryPlayerProfileRepository : PlayerProfileRepository {
    var isEnabled = true
    override val playersMap: ConcurrentHashMap<String, PlayerProfile> = ConcurrentHashMap()

    override suspend fun connect() {
       isEnabled = true
    }

    override suspend fun disconnect() {
        isEnabled = false
    }

    override suspend fun getPlayerProfile(name: String): PlayerProfile? {
        if (!isEnabled) return null
        return playersMap[name]
    }

    override suspend fun savePlayerProfile(profile: PlayerProfile) {
        if (!isEnabled) return
        playersMap[profile.name] = profile
    }

    override suspend fun compareAndUpdateProfiles() {
        playersMap.forEach { (name, dbProfile) ->
            val cachedProfile = playerProfiles[name]
            if (cachedProfile != null && cachedProfile != dbProfile) {
                savePlayerProfile(cachedProfile)
            }
        }
    }
}