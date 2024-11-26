package com.example.youtube_summary_native.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.youtube_summary_native.core.data.local.dao.SummaryDao
import com.example.youtube_summary_native.core.data.local.entity.ScriptItemConverter
import com.example.youtube_summary_native.core.data.local.entity.SummaryEntity

@Database(
    entities = [SummaryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(ScriptItemConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao

    companion object {
        const val DATABASE_NAME = "youtube_summary_app_database"
    }
}