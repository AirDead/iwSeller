package ru.airdead.iwseller.data

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.airdead.iwseller.quest.Quest
import ru.airdead.iwseller.quest.QuestStatus
import ru.airdead.iwseller.quest.QuestType
import ru.airdead.iwseller.quest.Reward
import java.util.concurrent.ConcurrentHashMap

data class PlayerProfile(
    val name: String,
    var level: Int,
    var experience: Int,
    var coins: Int = 0,
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
            if (it.status == QuestStatus.COMPLETED) return
            it.status = QuestStatus.COMPLETED
            gainRewards(it.reward)
            currentQuestIndex++
            checkLevelUp()
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

    fun gainRewards(reward: Reward) {
        coins += reward.coins
        var i = 0
        while (i < reward.soulsCount) {
            Bukkit.getPlayer(name)?.inventory?.addItem(
                ItemStack(Material.PAPER, 1)
            )
            i++
        }
        experience += reward.experience
    }

    fun checkLevelUp() {
        while (experience >= getExperienceForNextLevel()) {
            experience -= getExperienceForNextLevel()
            level++
        }
    }

    fun getExperienceForNextLevel(): Int {
        return level * 110
    }

    fun asString(): String {
        return """
            |Имя: $name
            |Уровень: $level
            |Опыт: $experience
            |Монеты: $coins
        """.trimMargin()
    }
}

val playerProfiles: ConcurrentHashMap<String, PlayerProfile> = ConcurrentHashMap()

val Player.profile: PlayerProfile
    get() = playerProfiles[this.name] ?: error("Profile for player ${this.name} not loaded!")
