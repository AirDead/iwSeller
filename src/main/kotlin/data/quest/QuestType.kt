package ru.airdead.iwseller.data.quest

import org.bukkit.Material
import org.bukkit.entity.EntityType

sealed class QuestType {
    data class Kill(val entityType: EntityType) : QuestType()
    data class Collect(val itemType: Material) : QuestType()
    data class Craft(val itemType: Material) : QuestType()
}