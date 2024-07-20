package ru.airdead.iwseller.data.quest

enum class QuestStatus(
    val displayName: String
) {
    NOT_STARTED("Не начат"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершен")
}