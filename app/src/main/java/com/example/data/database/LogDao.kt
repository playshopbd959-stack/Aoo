package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LogEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM system_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 120): Flow<List<LogEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<LogEventEntity>)

    @Query("DELETE FROM system_logs")
    suspend fun clearLogs()
}
