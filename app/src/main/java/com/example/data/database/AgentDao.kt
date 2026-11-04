package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents ORDER BY id ASC")
    fun getAllAgents(): Flow<List<AgentEntity>>

    @Query("SELECT * FROM agents ORDER BY id ASC")
    suspend fun getAllAgentsList(): List<AgentEntity>

    @Query("SELECT id FROM agents ORDER BY id ASC")
    suspend fun getAllAgentIds(): List<String>

    @Query("SELECT * FROM agents WHERE id = :id LIMIT 1")
    fun getAgentById(id: String): Flow<AgentEntity?>

    @Query("SELECT * FROM agents WHERE id = :id LIMIT 1")
    suspend fun getAgentByIdDirect(id: String): AgentEntity?

    @Query("SELECT * FROM agents WHERE cluster = :cluster ORDER BY id ASC")
    fun getAgentsByCluster(cluster: String): Flow<List<AgentEntity>>

    @Query("SELECT * FROM agents WHERE status = :status ORDER BY id ASC")
    fun getAgentsByStatus(status: String): Flow<List<AgentEntity>>

    @Query("SELECT * FROM agents WHERE id LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchAgents(query: String): Flow<List<AgentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(agents: List<AgentEntity>)

    @Update
    suspend fun updateAgent(agent: AgentEntity)

    @Query("UPDATE agents SET status = :status, currentTask = :currentTask, lastActive = :lastActive WHERE id = :id")
    suspend fun updateAgentStatus(id: String, status: String, currentTask: String, lastActive: Long = System.currentTimeMillis())

    @Query("UPDATE agents SET status = :status, currentTask = :task, loadPercent = :load, lastExecutionOutput = :output, lastActive = :time WHERE id = :id")
    suspend fun updateAgentExecution(id: String, status: String, task: String, load: Int, output: String?, time: Long = System.currentTimeMillis())

    @Query("UPDATE agents SET status = :status, completedTasks = completedTasks + 1, currentTask = 'Task completed', loadPercent = 5, lastExecutionOutput = :output, lastActive = :time WHERE id = :id")
    suspend fun recordTaskSuccess(id: String, output: String, time: Long = System.currentTimeMillis(), status: String = "COMPLETED")

    @Query("UPDATE agents SET status = 'FAILED', failedTasks = failedTasks + 1, currentTask = 'Execution error', loadPercent = 0, lastExecutionOutput = :errorMsg, lastActive = :time WHERE id = :id")
    suspend fun recordTaskFailure(id: String, errorMsg: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE agents SET status = :status, currentTask = :task, lastActive = :time")
    suspend fun updateAllStatus(status: String, task: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE agents SET status = :status, currentTask = :task, lastActive = :time WHERE cluster = :cluster")
    suspend fun updateClusterStatus(cluster: String, status: String, task: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE agents SET status = 'IDLE', currentTask = 'Self-healed by Watchdog', lastActive = :time WHERE status = 'FAILED' OR status = 'ERROR'")
    suspend fun recoverAllErrors(time: Long = System.currentTimeMillis()): Int

    @Query("SELECT COUNT(*) FROM agents")
    suspend fun countTotal(): Int

    @Query("SELECT COUNT(*) FROM agents WHERE status = :status")
    fun countByStatus(status: String): Flow<Int>

    @Query("SELECT * FROM agents WHERE status = 'ACTIVE' OR status = 'IDLE' OR status = 'COMPLETED' ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomAvailableAgents(limit: Int): List<AgentEntity>
}
