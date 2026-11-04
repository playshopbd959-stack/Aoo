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

    @Query("UPDATE agents SET status = :status, currentTask = :task, lastActive = :time")
    suspend fun updateAllStatus(status: String, task: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE agents SET status = :status, currentTask = :task, lastActive = :time WHERE cluster = :cluster")
    suspend fun updateClusterStatus(cluster: String, status: String, task: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE agents SET status = 'ACTIVE', currentTask = 'Recovered by Watchdog', failedTasks = failedTasks, lastActive = :time WHERE status = 'ERROR'")
    suspend fun recoverAllErrors(time: Long = System.currentTimeMillis()): Int

    @Query("SELECT COUNT(*) FROM agents")
    suspend fun countTotal(): Int

    @Query("SELECT COUNT(*) FROM agents WHERE status = :status")
    fun countByStatus(status: String): Flow<Int>

    @Query("SELECT * FROM agents WHERE status = 'ACTIVE' OR status = 'IDLE' ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomAvailableAgents(limit: Int): List<AgentEntity>
}
