package com.nikolapehnec.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nikolapehnec.model.ReviewEntity
import com.nikolapehnec.model.ShowEntity

@Database(
    entities = [ShowEntity::class, ReviewEntity::class],
    version = 3
)
abstract class ShowsDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: ShowsDatabase? = null

        fun getDatabase(context: Context): ShowsDatabase? {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context,
                    ShowsDatabase::class.java,
                    "shows_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = database
                database
            }
        }
    }

    abstract fun showsDao(): ShowsDao

    abstract fun reviewsDao(): ReviewDao

}