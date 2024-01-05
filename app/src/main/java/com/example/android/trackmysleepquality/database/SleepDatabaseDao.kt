package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


// Defines methods for using the SleepNight class with Room.
// When we use a room database we query the database by defining and calling Kotlin functions
// that maps to SQL queries and we define those mapping using annotations

@Dao
interface SleepDatabaseDao {

    // To insert new night data we can directly used insert function via Insert annotation
    @Insert
    suspend fun insert(night: SleepNight) // -> when we will call insert(___) then sql insert query works

    // When updating a row with a value already set in a column, replaces the old value with the new one.
    @Update
    suspend fun update(night: SleepNight)

    //Selects and returns the row that matches the supplied start time, which is our key.
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key") // for query write SQL code
    suspend fun get(key: Long): SleepNight?

    // Deletes all values from the table. This does not delete the table, only its contents.
    @Query("DELETE FROM daily_sleep_quality_table")
    suspend fun clear()

     // Selects and returns all rows in the table, sorted by start time in descending order.
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>

    // Selects and returns the latest night.
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    suspend fun getTonight(): SleepNight?

    // Selects and returns the night with given nightId.
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun getNightWithId(key: Long): LiveData<SleepNight>
}