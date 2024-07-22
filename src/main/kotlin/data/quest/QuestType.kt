package ru.airdead.iwseller.data.quest

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bukkit.Material
import org.bukkit.entity.EntityType

sealed class QuestType {
    data class Kill(val entityType: EntityType) : QuestType()
    data class Collect(val itemType: Material) : QuestType()
    data class Craft(val itemType: Material) : QuestType()
}

class QuestTypeCodec : Codec<QuestType> {
    override fun encode(writer: BsonWriter, value: QuestType, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        when (value) {
            is QuestType.Kill -> {
                writer.writeString("type", "Kill")
                writer.writeString("entityType", value.entityType.name)
            }
            is QuestType.Collect -> {
                writer.writeString("type", "Collect")
                writer.writeString("itemType", value.itemType.name)
            }
            is QuestType.Craft -> {
                writer.writeString("type", "Craft")
                writer.writeString("itemType", value.itemType.name)
            }

        }
        writer.writeEndDocument()
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): QuestType {
        reader.readStartDocument()
        val type = reader.readString("type")
        val questType = when (type) {
            "Kill" -> QuestType.Kill(EntityType.valueOf(reader.readString("entityType")))
            "Collect" -> QuestType.Collect(Material.valueOf(reader.readString("itemType")))
            "Craft" -> QuestType.Craft(Material.valueOf(reader.readString("itemType")))
            else -> throw IllegalArgumentException("Unknown QuestType: $type")
        }
        reader.readEndDocument()
        return questType
    }

    override fun getEncoderClass(): Class<QuestType> {
        return QuestType::class.java
    }
}
