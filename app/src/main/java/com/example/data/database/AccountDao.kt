package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.GoogleAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM google_accounts ORDER BY addedTimestamp DESC")
    fun getAllAccounts(): Flow<List<GoogleAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: GoogleAccountEntity)

    @Query("UPDATE google_accounts SET isConnected = :connected, lastSyncTimestamp = :syncTime WHERE email = :email")
    suspend fun updateConnectionStatus(email: String, connected: Boolean, syncTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM google_accounts WHERE email = :email")
    suspend fun deleteAccount(email: String)

    @Query("SELECT COUNT(*) FROM google_accounts")
    suspend fun countAccounts(): Int
}
