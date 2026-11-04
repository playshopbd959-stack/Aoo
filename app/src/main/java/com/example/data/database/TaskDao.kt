package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun getTaskById(id: String): Flow<TaskEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET progress = :progress, status = :status, completedAt = :completedAt, executionTimeMs = :execTime, resultSummary = :result WHERE id = :id")
    suspend fun updateTaskProgress(id: String, progress: Float, status: String, completedAt: Long?, execTime: Long, result: String?)

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'")
    fun countCompletedTasks(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'FAILED'")
    fun countFailedTasks(): Flow<Int>

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)
}
