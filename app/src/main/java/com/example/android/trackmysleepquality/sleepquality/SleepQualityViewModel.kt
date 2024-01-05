package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.*

// this SleepQualityViewModel contains two parameters one is sleepNightId and other database dao
class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao
) : ViewModel() {

    // job is defined which will cancel all the coroutines
    private val viewModelJob = Job()

    // uiScope defined for the coroutine on the main thread and also tells about viewModel job
    private val uiScope =  CoroutineScope(Dispatchers.Main + viewModelJob)

    // live data variable to navigate to sleepTracker again
    private val _navigateToSleepTracker =  MutableLiveData<Boolean?>()
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    // function to record the sleep quality for the latest night
    // this function will be used as a click handler on the icons present on the sleep_quality.xml layout file
    fun onSetSleepQuality(quality: Int) {
        // launch coroutine that run on main ui thread
        uiScope.launch {
            // long running work nothing to do with main ui so launch coroutine on any other io dispatcher
            withContext(Dispatchers.IO) {
                // database long running work is done here
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }
            // after updating this we can navigate back to the sleepTracker so make this
            // navigate variable to be true
            _navigateToSleepTracker.value = true
        }
    }

    // to confirm that navigation from sleepQuality to sleepTrakcer is done
    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    // this function called when this view Model is destroyed
    override fun onCleared() {
        super.onCleared()
        // viewModelJob will cancel all the coroutines
        viewModelJob.cancel()
    }
}