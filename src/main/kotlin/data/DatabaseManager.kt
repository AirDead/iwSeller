package ru.airdead.iwseller.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import ru.airdead.iwseller.data.quest.Quest
import ru.airdead.iwseller.data.quest.QuestStatus
import ru.airdead.iwseller.data.quest.QuestType
import ru.airdead.iwseller.data.quest.Reward
import java.sql.Connection
import java.sql.DriverManager

object DatabaseManager {
    private const val DATABASE_URL = "jdbc:sqlite:players.db"
    private lateinit var connection: Connection
    fun initialize() {
        val logger = Bukkit.getLogger()
        try {
            connection = DriverManager.getConnection(DATABASE_URL)
            createTables()
            logger.info("Database connected and tables created.")
        } catch (e: Exception) {
            logger.severe("Failed to connect to the database: ${e.message}")
        }
    }

    private fun createTables() {
        connection.createStatement().use { stmt ->
            stmt.execute(
                """CREATE TABLE IF NOT EXISTS players (
                    name TEXT PRIMARY KEY,
                    level INTEGER,
                    experience INTEGER,
                    coins INTEGER
                )"""
            )
            stmt.execute(
                """CREATE TABLE IF NOT EXISTS quests (
                    player_name TEXT,
                    quest_name TEXT,
                    description TEXT,
                    reward_coins INTEGER,
                    reward_soulsCount INTEGER,
                    reward_experience INTEGER,
                    type TEXT,
                    target INTEGER,
                    progress INTEGER,
                    status TEXT,
                    PRIMARY KEY (player_name, quest_name),
                    FOREIGN KEY (player_name) REFERENCES players(name)
                )"""
            )
        }
    }

    suspend fun savePlayerProfile(profile: PlayerProfile) {
        withContext(Dispatchers.IO) {
            connection.prepareStatement(
                """INSERT OR REPLACE INTO players (name, level, experience, coins)
                   VALUES (?, ?, ?, ?)"""
            ).use { stmt ->
                stmt.setString(1, profile.name)
                stmt.setInt(2, profile.level)
                stmt.setInt(3, profile.experience)
                stmt.setInt(4, profile.coins)
                stmt.executeUpdate()
            }

            connection.prepareStatement(
                "DELETE FROM quests WHERE player_name = ?"
            ).use { stmt ->
                stmt.setString(1, profile.name)
                stmt.executeUpdate()
            }

            profile.quests.forEach { quest ->
                connection.prepareStatement(
                    """INSERT INTO quests (player_name, quest_name, description, reward_coins, reward_soulsCount,
                        reward_experience, type, target, progress, status)
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
                ).use { stmt ->
                    stmt.setString(1, profile.name)
                    stmt.setString(2, quest.name)
                    stmt.setString(3, quest.description)
                    stmt.setInt(4, quest.reward.coins)
                    stmt.setInt(5, quest.reward.soulsCount)
                    stmt.setInt(6, quest.reward.experience)
                    stmt.setString(7, quest.type::class.simpleName)
                    stmt.setInt(8, quest.target)
                    stmt.setInt(9, quest.progress)
                    stmt.setString(10, quest.status.name)
                    stmt.executeUpdate()
                }
            }
        }
    }

    suspend fun loadPlayerProfile(name: String): PlayerProfile? {
        return withContext(Dispatchers.IO) {
            connection.prepareStatement(
                "SELECT * FROM players WHERE name = ?"
            ).use { stmt ->
                stmt.setString(1, name)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val profile = PlayerProfile(
                        name = rs.getString("name"),
                        level = rs.getInt("level"),
                        experience = rs.getInt("experience"),
                        coins = rs.getInt("coins")
                    )

                    connection.prepareStatement(
                        "SELECT * FROM quests WHERE player_name = ?"
                    ).use { questStmt ->
                        questStmt.setString(1, name)
                        val questRs = questStmt.executeQuery()
                        while (questRs.next()) {
                            val quest = Quest(
                                name = questRs.getString("quest_name"),
                                description = questRs.getString("description"),
                                reward = Reward(
                                    coins = questRs.getInt("reward_coins"),
                                    soulsCount = questRs.getInt("reward_soulsCount"),
                                    experience = questRs.getInt("reward_experience")
                                ),
                                type = when (questRs.getString("type")) {
                                    "Kill" -> QuestType.Kill(EntityType.valueOf(questRs.getString("type")))
                                    "Collect" -> QuestType.Collect(Material.valueOf(questRs.getString("type")))
                                    "Craft" -> QuestType.Craft(Material.valueOf(questRs.getString("type")))
                                    else -> throw IllegalArgumentException("Unknown quest type")
                                },
                                target = questRs.getInt("target"),
                                progress = questRs.getInt("progress"),
                                status = QuestStatus.valueOf(questRs.getString("status"))
                            )
                            profile.quests.add(quest)
                        }
                    }

                    profile
                } else {
                    null
                }
            }
        }
    }

    fun closeConnection() {
        val logger = Bukkit.getLogger()
        try {
            if (::connection.isInitialized && !connection.isClosed) {
                connection.close()
                logger.info("Database connection closed.")
            }
        } catch (e: Exception) {
            logger.severe("Failed to close the database connection: ${e.message}")
        }
    }
}
