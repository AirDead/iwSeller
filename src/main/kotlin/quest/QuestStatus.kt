package ru.airdead.iwseller.quest

enum class QuestStatus(
    val displayName: String
) {
    NOT_STARTED("Не начат"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершен")
}