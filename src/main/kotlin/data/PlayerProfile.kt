package ru.airdead.iwseller.data

import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

data class PlayerProfile(
    val name: String,
    val level: Int,
    val experience: Int,
    var quests: MutableList<Quest> = mutableListOf(),
    var currentQuestIndex: Int = 0
) {
    fun assignNextQuest(): Boolean {
        if (hasActiveQuest()) return false
        if (currentQuestIndex >= quests.size) return false
        quests[currentQuestIndex].status = QuestStatus.IN_PROGRESS
        return true
    }

    fun completeQuest(questName: String) {
        quests.find { it.name == questName }?.let {
            it.status = QuestStatus.COMPLETED
            currentQuestIndex++
        }
    }

    fun updateQuestProgress(questType: QuestType) {
        quests.find { it.type == questType && it.status == QuestStatus.IN_PROGRESS }?.incrementProgress()
    }

    fun getQuestStatus(questName: String): QuestStatus? {
        return quests.find { it.name == questName }?.status
    }

    fun getCurrentQuest(): Quest? {
        return if (currentQuestIndex < quests.size) quests[currentQuestIndex] else null
    }

    fun hasActiveQuest(): Boolean {
        return quests.any { it.status == QuestStatus.IN_PROGRESS }
    }

    fun asString(): String {
        return """
            |Имя: $name
            |Уровень: $level
            |Опыт: $experience
            |Количество задач: ${quests.size}
        """.trimMargin()
    }
}

private val playerProfiles: ConcurrentHashMap<String, PlayerProfile> = ConcurrentHashMap()

val Player.profile: PlayerProfile
    get() = playerProfiles.getOrPut(this.name) {
        PlayerProfile(name = this.name, level = 1, experience = 0)
    }
