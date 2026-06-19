package com.calldad.boast.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ComplimentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ComplimentDatabase : RoomDatabase() {

    abstract fun complimentDao(): ComplimentDao

    companion object {
        @Volatile
        private var INSTANCE: ComplimentDatabase? = null

        fun getDatabase(context: Context): ComplimentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ComplimentDatabase::class.java,
                    "compliment_database"
                )
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}