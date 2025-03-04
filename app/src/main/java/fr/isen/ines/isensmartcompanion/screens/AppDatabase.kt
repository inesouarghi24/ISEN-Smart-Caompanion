package fr.isen.ines.isensmartcompanion.database

import android.content.Context
import androidx.room.*
import fr.isen.ines.isensmartcompanion.screens.ChatHistoryDao
import fr.isen.ines.isensmartcompanion.screens.ChatHistoryEntity
import fr.isen.ines.isensmartcompanion.screens.CourseDao
import fr.isen.ines.isensmartcompanion.screens.CourseEntity
import fr.isen.ines.isensmartcompanion.screens.CustomEventDao
import fr.isen.ines.isensmartcompanion.screens.CustomEventEntity

@Database(
    entities = [CustomEventEntity::class, CourseEntity::class, ChatHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customEventDao(): CustomEventDao
    abstract fun courseDao(): CourseDao
    abstract fun chatHistoryDao(): ChatHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "app_database"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
