package ru.airdead.iwseller.data.mongodb

import com.mongodb.ConnectionString
import com.mongodb.KotlinCodecProvider
import com.mongodb.MongoClientSettings
import com.mongodb.MongoException
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import ru.airdead.iwseller.data.PlayerProfile
import ru.airdead.iwseller.data.PlayerProfileRepository
import ru.airdead.iwseller.data.mongodb.codec.QuestTypeCodec
import ru.airdead.iwseller.data.playerProfiles
import java.util.concurrent.ConcurrentHashMap

class MongoPlayerProfileRepository : PlayerProfileRepository {
    lateinit var mongoClient: MongoClient
    lateinit var database: MongoDatabase
    lateinit var collection: MongoCollection<PlayerProfile>
    override val playersMap: ConcurrentHashMap<String, PlayerProfile> = ConcurrentHashMap()

    private val customCodecs: CodecRegistry = CodecRegistries.fromRegistries(
        CodecRegistries.fromCodecs(QuestTypeCodec()),
        CodecRegistries.fromProviders(KotlinCodecProvider()),
        MongoClientSettings.getDefaultCodecRegistry()
    )

    private val settings = MongoClientSettings.builder()
        .codecRegistry(customCodecs)
        .applyConnectionString(ConnectionString("mongodb+srv://airdead:CcSvjuw6bGksXCRq@iwworld.ywhadv4.mongodb.net/?retryWrites=true&w=majority&appName=IwWorld"))
        .applyToLoggerSettings {
            it.maxDocumentLength(0)
        }
        .build()


    override suspend fun connect() {
        mongoClient = MongoClient.create(settings)
        database = mongoClient.getDatabase("iw-seller")
        collection = database.getCollection<PlayerProfile>("playerProfiles")
    }

    override suspend fun disconnect() {
        mongoClient.close()
    }

    override suspend fun getPlayerProfile(name: String): PlayerProfile? {
        val profile = collection.find(eq("name", name)).firstOrNull()
        if (profile != null) {
            playersMap[name] = profile
        }
        return profile
    }

    override suspend fun savePlayerProfile(profile: PlayerProfile) {
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

    override suspend fun compareAndUpdateProfiles() {
        playersMap.forEach { (name, dbProfile) ->
            val cachedProfile = playerProfiles[name]
            if (cachedProfile != null && cachedProfile != dbProfile) {
                savePlayerProfile(cachedProfile)
            }
        }
    }
}