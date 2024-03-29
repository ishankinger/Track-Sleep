package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


// A database that stores SleepNight information.
// And a global method to get access to the database.
// This pattern is pretty much the same for any database,so we can reuse it.

@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    // Connects the database to the DAO.
    abstract val sleepDatabaseDao: SleepDatabaseDao

    // Define a companion object, this allows us to add functions on the SleepDatabase class.
    // For example, clients can call `SleepDatabase.getInstance(context)` to instantiate a new SleepDatabase.
    companion object {
         // INSTANCE will keep a reference to any database returned via getInstance.
         // This will help us avoid repeatedly initializing the database, which is expensive.
         // The value of a volatile variable will never be cached, and all writes and
         // reads will be done to and from the main memory. It means that changes made by one
         // thread to shared data are visible to other threads.
        @Volatile // -> value of instance is always up to date
        private var INSTANCE: SleepDatabase? = null

        // Helper function to get the database.
        // If a database has already been retrieved, the previous database will be returned.Otherwise, create a new database.
        // This function is threadsafe, and callers should cache the result for multiple database calls to avoid overhead.
        fun getInstance(context: Context): SleepDatabase {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a time.
            synchronized(this) {
                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // Smart cast is only available to local variables.
                var instance = INSTANCE
                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_history_database"
                    )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        .fallbackToDestructiveMigration()
                        .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }
                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}


// -> Direct Code to build any Room Database


//package com.example.android.trackmysleepquality.database
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
//abstract class SleepDatabase : RoomDatabase() {
//
//    abstract val sleepDatabaseDao: SleepDatabaseDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: SleepDatabase? = null
//
//        fun getInstance(context: Context): SleepDatabase {
//            synchronized(this) {
//                var instance = INSTANCE
//                if (instance == null) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        SleepDatabase::class.java,
//                        "sleep_history_database"
//                    ).fallbackToDestructiveMigration().build()
//                    INSTANCE = instance
//                }
//                return instance
//            }
//        }
//    }
//}