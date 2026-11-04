package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "google_accounts")
data class GoogleAccountEntity(
    @PrimaryKey
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val authorizedScopes: String = "https://www.googleapis.com/auth/youtube.readonly, https://www.googleapis.com/auth/gmail.send",
    val isConnected: Boolean = true,
    val accountType: String = "OAuth2 Google Account",
    val addedTimestamp: Long = System.currentTimeMillis(),
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
