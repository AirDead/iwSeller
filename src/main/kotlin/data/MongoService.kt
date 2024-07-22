package ru.airdead.iwseller.data

import com.mongodb.ConnectionString
import com.mongodb.KotlinCodecProvider
import com.mongodb.MongoClientSettings
import com.mongodb.MongoException
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.firstOrNull
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import ru.airdead.iwseller.data.quest.QuestTypeCodec
import java.util.concurrent.ConcurrentHashMap

object MongoService {
    val customCodecs: CodecRegistry = CodecRegistries.fromRegistries(
        CodecRegistries.fromCodecs(QuestTypeCodec()),
        CodecRegistries.fromProviders(KotlinCodecProvider()),
        MongoClientSettings.getDefaultCodecRegistry()
    )

    val settings = MongoClientSettings.builder()
        .codecRegistry(customCodecs)
        .applyConnectionString(ConnectionString("mongodb+srv://airdead:lrhPjhjfTpSPdiVO@cluster0.559bmea.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"))
        .applyToLoggerSettings { it.maxDocumentLength(0) }
        .build()

    val mongoClient = MongoClient.create(settings)
    val database = mongoClient.getDatabase("iw-seller")
    val collection = database.getCollection<PlayerProfile>("playerProfiles")
    val playersMap : ConcurrentHashMap<String, PlayerProfile> = ConcurrentHashMap()

    suspend fun getPlayerProfile(name: String): PlayerProfile? {
        val profile = collection.find(eq("name", name)).firstOrNull()
        if (profile != null) {
            playersMap[name] = profile
        }
        return profile
    }

    suspend fun savePlayerProfile(profile: PlayerProfile) {
        val filter = eq("name", profile.name)
        val updates = combine(
            set("level", profile.level),
            set("experience", profile.experience),
            set("coins", profile.coins),
            set("quests", profile.quests),
            set("currentQuestIndex", profile.currentQuestIndex)
        )
        val options = UpdateOptions().upsert(true)

        playersMap[profile.name] = profile
        try {
            collection.updateOne(filter, updates, options)
        } catch (e: MongoException) {
            throw e
        }
    }
}
