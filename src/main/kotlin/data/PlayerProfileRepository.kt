package ru.airdead.iwseller.data

import java.util.concurrent.ConcurrentHashMap

interface PlayerProfileRepository {
    val playersMap: ConcurrentHashMap<String, PlayerProfile>

    suspend fun getPlayerProfile(name: String): PlayerProfile?
    suspend fun savePlayerProfile(profile: PlayerProfile)
    suspend fun connect()
    suspend fun disconnect()
    suspend fun compareAndUpdateProfiles()
}