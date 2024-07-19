package ru.airdead.iwseller.data

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class QuestType {
    KILL,
    COLLECT,
    CRAFT
}

enum class QuestStatus(
    val displayName: String
) {
    NOT_STARTED("Не начат"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершен")
}

data class Quest(
    val name: String,
    val description: String,
    val reward: String,
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
                    "Награда:",
                    reward,
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
