package com.example.youtube_summary_native.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.youtube_summary_native.core.data.local.entity.SummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {
    @Query("SELECT * FROM summaries ORDER BY createdAt DESC")
    fun getAllSummaries(): Flow<List<SummaryEntity>>

    @Query("SELECT * FROM summaries WHERE videoId = :videoId")
    suspend fun getSummaryById(videoId: String): SummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity): Long

    @Update
    suspend fun updateSummary(summary: SummaryEntity): Int

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummaries(summaries: List<SummaryEntity>)

    @Query("DELETE FROM summaries WHERE videoId = :videoId")
    suspend fun deleteSummary(videoId: String): Int

    @Query("DELETE FROM summaries")
    suspend fun deleteAllSummaries(): Int

    // 유틸리티 함수들
    @Transaction
    suspend fun insertOrUpdateSummary(summary: SummaryEntity): Boolean {
        return try {
            insertSummary(summary)
            true
        } catch (e: Exception) {
            false
        }
    }

    @Transaction
    suspend fun insertOrUpdateSummaries(summaries: List<SummaryEntity>): Boolean {
        return try {
            insertSummaries(summaries)
            true
        } catch (e: Exception) {
            false
        }
    }
}