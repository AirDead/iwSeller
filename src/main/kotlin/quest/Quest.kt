package ru.airdead.iwseller.quest

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class Quest(
    val name: String,
    val description: String,
    val reward: Reward,
    val questType: QuestType,
    var progress: Int = 0,
    var status: QuestStatus = QuestStatus.NOT_STARTED
) {
    fun toItemStack(): ItemStack {
        return ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName(name)
                lore = listOf(
                    description,
                    "\n",
                    "Награда:",
                    "${reward.coins} монет",
                    "Душ: ${reward.soulsCount}",
                    "Опыт: ${reward.experience}",
                    "\n",
                    "Прогресс: $progress/${questType.target}",
                    "Статус: ${status.displayName}"
                )
            }
        }
    }

    fun incrementProgress() {
        if (status == QuestStatus.IN_PROGRESS) {
            progress++
            if (progress >= questType.target) {
                status = QuestStatus.COMPLETED
            }
        }
    }
}