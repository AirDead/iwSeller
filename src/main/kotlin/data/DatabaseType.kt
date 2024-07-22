package ru.airdead.iwseller.data

enum class DatabaseType(
    val displayName: String
) {
    MONGODB("MongoDB"),
    IN_MEMORY("In-Memory Database")
}