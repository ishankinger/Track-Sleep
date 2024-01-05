package com.example.android.trackmysleepquality.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//We use data class to define our table and annotation to specify things

@Entity(tableName = "daily_sleep_quality_table")      // -> name of the table
data class SleepNight(                                // -> different columns are defined
    @PrimaryKey(autoGenerate = true)                  // -> annotate their respective properties
    var nightId: Long = 0L,

    @ColumnInfo(name = "start_time_milli")
    val startTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli,

    @ColumnInfo(name = "quality_rating")
    var sleepQuality: Int = -1
)

// These four columns will store information about different nights
// -> nightId, startTime, endTime, sleepQuality