package ru.airdead.iwseller.data.mongodb.codec

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bukkit.Material
import org.bukkit.entity.EntityType
import ru.airdead.iwseller.quest.QuestType

class QuestTypeCodec : Codec<QuestType> {
    override fun encode(writer: BsonWriter, value: QuestType, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeInt32("target", value.target)
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
        val target = reader.readInt32("target")
        val type = reader.readString("type")
        val questType = when (type) {
            "Kill" -> QuestType.Kill(EntityType.valueOf(reader.readString("entityType")), target)
            "Collect" -> QuestType.Collect(Material.valueOf(reader.readString("itemType")), target)
            "Craft" -> QuestType.Craft(Material.valueOf(reader.readString("itemType")), target)
            else -> throw IllegalArgumentException("Unknown QuestType: $type")
        }
        reader.readEndDocument()
        return questType
    }

    override fun getEncoderClass(): Class<QuestType> {
        return QuestType::class.java
    }
}
