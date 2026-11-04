package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.VideoAnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM video_analyses ORDER BY analyzedAt DESC")
    fun getAllAnalyses(): Flow<List<VideoAnalysisEntity>>

    @Query("SELECT * FROM video_analyses WHERE id = :id LIMIT 1")
    fun getAnalysisById(id: String): Flow<VideoAnalysisEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: VideoAnalysisEntity)

    @Query("DELETE FROM video_analyses WHERE id = :id")
    suspend fun deleteAnalysis(id: String)
}
