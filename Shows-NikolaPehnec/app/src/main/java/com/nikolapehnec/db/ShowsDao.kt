package com.nikolapehnec.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nikolapehnec.model.ShowEntity

@Dao
interface ShowsDao {

    @Query("SELECT * FROM show")
    fun getAllShows(): LiveData<List<ShowEntity>>

    @Query("SELECT * FROM show")
    fun getAllShowsNL(): List<ShowEntity>

    @Query("SELECT * FROM show WHERE id IS :id")
    fun getShowById(id: Int): LiveData<ShowEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllShows(shows: List<ShowEntity>)
}