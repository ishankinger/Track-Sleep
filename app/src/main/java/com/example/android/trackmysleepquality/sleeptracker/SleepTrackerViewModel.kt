package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.view.animation.Transformation
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*


// ViewModel for SleepTrackerFragment.
// This View Model contains two parameters one of database and other application context
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

        // This viewModelJob allows us to cancel all coroutines started by this View Model when it get destroyed
        private var viewModelJob = Job()  // used in onCleared function at the end of this code

        // The scope determines what thread the coroutine will run on and it also needs to know about the job
        private val uiscope = CoroutineScope(Dispatchers.Main + viewModelJob)
        // Dispatchers.Main means coroutine will run on the main thread

        // variable to hold the latest night and stored as MutableLiveData so that it can be changed
        private var tonight = MutableLiveData<SleepNight>()

        // give all the nights of the database and this function is used in SleepDatabase Dao
        val nights = database.getAllNights()

        // format the data of the nights with the help of formatNights function
        val nightsString = Transformations.map(nights) { nights ->
                formatNights(nights, application.resources)
        }

        // live data variable to navigate from sleepTracker to sleepQuality fragment
        private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        val navigateToSleepQuality : LiveData<SleepNight>
                get() = _navigateToSleepQuality

        // live data variable to show snack bar
        private var _showSnackbarEvent = MutableLiveData<Boolean>()
        val showSnackBarEvent: LiveData<Boolean>
                get() = _showSnackbarEvent

        private val _navigateToSleepDataQuality = MutableLiveData<Long>()
        val navigateToSleepDataQuality
                get() = _navigateToSleepDataQuality

        // Button States
        // -> start Button visible when latest night is null
        val startButtonVisible = Transformations.map(tonight){
                it == null
        }
        // -> stop Button visible when latest night is not null
        val stopButtonVisible = Transformations.map(tonight){
                it != null
        }
        // -> clear button is visible when list containing all nights is not null
        val clearButtonVisible = Transformations.map(nights){
                it?.isNotEmpty()
        }

        // we want to initialise the tonight as soon so written in init block
        init{
                initializeTonight()
        }

        // we launch a coroutine without blocking current thread
        private fun initializeTonight(){
                // inside we are using a coroutine to get tonight so that we are not blocking the ui
                // while waiting for other results
                uiscope.launch{
                        tonight.value = getTonightFromDatabase()
                }
        }

        // we mark it as suspend because we want to call it from inside the coroutine and not block
        private suspend fun getTonightFromDatabase() : SleepNight?{
                // we actually create other coroutine in io context
                return withContext(Dispatchers.IO){
                        // database function of getting latest night
                        var night = database.getTonight()
                        if(night?.endTimeMilli != night?.startTimeMilli){
                                night = null
                        }
                        night
                }
        }

        // this onStartTracking is for startButton
        // These functions are linked to the button directly in the xml code thanks to data binding
        fun onStartTracking(){                                           // -> some function need to be done
                uiscope.launch{                           // -> used for not blocking the ui
                        val newNight = SleepNight()
                        insert(newNight)                                 // usual code written inside it
                        tonight.value = getTonightFromDatabase()         // but we will be using suspend function
                }
        }
        // suspend function for insert
        private suspend fun insert(night : SleepNight){                  // suspend function includes time consuming operations
                withContext(Dispatchers.IO){             //  create another coroutine
                        database.insert(night)                           // database time consuming operation used
                }
        }

        // Basic Syntax to use coroutine for long running work
        //------------------------------------------------------------------------------------------------------------
        fun someNeedToBeDone(){
                // launch coroutine that runs on the main UI thread because the result sholud affect UI
                uiscope.launch{
                        // this function is used to do long running work which donot block UI thread and
                        suspendFunction()
                }
        }
        suspend fun suspendFunction(){
                // this long running work has nothing to do with UI so we are switching to Dispatchers.IO
                withContext(Dispatchers.IO){
                        // this longRunningWork now will not be blocked
                        longRunningWork()
                }
        }
        fun longRunningWork(){
                // work such as fetching data from internet, reading large files or writting data to database
        }

        // -----------------------------------------------------------------------------------------------------------

        // this function is for stopButton
        fun onStopTracking(){
                uiscope.launch{
                        val oldNight = tonight.value?:return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)
                        // making this variable non-null so that navigation occurs
                        _navigateToSleepQuality.value = oldNight
                }
        }
        // suspended function for onStopTracking
        private suspend fun update(night : SleepNight){
                withContext(Dispatchers.IO){
                        database.update(night)
                }
        }

        // function for clear button
        fun onClear(){
                uiscope.launch{
                        clear()
                        tonight.value = null
                }
        }
        // suspended function for onClear
        private suspend fun clear(){
                withContext(Dispatchers.IO){
                        database.clear()
                }
        }

        // function to tell that navigation is done from sleepTracker to sleepQuality
        fun doneNavigating(){
                _navigateToSleepQuality.value = null
        }

        // function to tell that snack bar even is done
        fun doneShowingSnackbar() {
                _showSnackbarEvent.value = false
        }

        fun onSleepNightClicked(id: Long){
                _navigateToSleepDataQuality.value = id
        }

        fun onSleepDataQualityNavigated() {
                _navigateToSleepDataQuality.value = null
        }

        // When a viewModel is destroyed onCleared is called
        override fun onCleared() {
                super.onCleared()
                // here we tell the viewModelJob to cancel all coroutines
                viewModelJob.cancel()
                // to trigger the _showSnackbarEvent
                _showSnackbarEvent.value = true
        }
}

