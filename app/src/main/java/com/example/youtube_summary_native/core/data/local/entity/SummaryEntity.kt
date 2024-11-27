package com.example.youtube_summary_native.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.youtube_summary_native.core.domain.model.summary.ScriptItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey
    val videoId: String,
    val title: String,
    val summary: String,
    val rawScript: String,
    val thumbnailUrl: String,
    val status: String,
    val createdAt: String
)

class ScriptItemConverter {
    @TypeConverter
    fun fromScriptItems(value: List<ScriptItem>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toScriptItems(value: String): List<ScriptItem> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}