package ru.airdead.iwseller.quest

import org.bukkit.Material
import org.bukkit.entity.EntityType

sealed class QuestType {
    abstract val target: Int

    data class Kill(val entityType: EntityType, override val target: Int) : QuestType()
    data class Collect(val itemType: Material, override val target: Int) : QuestType()
    data class Craft(val itemType: Material, override val target: Int) : QuestType()
}