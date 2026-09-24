package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UploadBatchEntity::class], version = 1, exportSchema = false)
abstract class RugbyDatabase : RoomDatabase() {
    abstract fun uploadBatchDao(): UploadBatchDao

    companion object {
        @Volatile
        private var INSTANCE: RugbyDatabase? = null

        fun getInstance(context: Context): RugbyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RugbyDatabase::class.java,
                    "rugby_photos_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
