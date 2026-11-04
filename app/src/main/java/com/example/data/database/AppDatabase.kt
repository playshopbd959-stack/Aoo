package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AgentEntity
import com.example.data.model.GoogleAccountEntity
import com.example.data.model.LogEventEntity
import com.example.data.model.TaskEntity
import com.example.data.model.VideoAnalysisEntity

@Database(
    entities = [
        AgentEntity::class,
        TaskEntity::class,
        LogEventEntity::class,
        VideoAnalysisEntity::class,
        GoogleAccountEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao
    abstract fun taskDao(): TaskDao
    abstract fun logDao(): LogDao
    abstract fun videoDao(): VideoDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xsos_agent_system.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
