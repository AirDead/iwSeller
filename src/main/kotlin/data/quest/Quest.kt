package ru.airdead.iwseller.data.quest

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class Quest(
    val name: String,
    val description: String,
    val reward: Reward,
    val type: QuestType,
    val target: Int,
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
                    "Прогресс: $progress/$target",
                    "Статус: ${status.displayName}"
                )
            }
        }
    }

    fun incrementProgress() {
        if (status == QuestStatus.IN_PROGRESS) {
            progress++
            if (progress >= target) {
                status = QuestStatus.COMPLETED
            }
        }
    }
}