package com.example.android.trackmysleepquality.sleepquality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding

//Fragment that displays a list of clickable icons,each representing a sleep quality rating.
//Once the user taps an icon, the quality is set in the current sleepNight and the database is updated.
class SleepQualityFragment : Fragment() {
//    Called when the Fragment is ready to display content to the screen.
//    This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_quality, container, false)

        // get the application context
        val application = requireNotNull(this.activity).application

        // from instance of database getting all data in it
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // from safe args getting the data that is transferred ( getting sleepNightId as bundle in navigation )
        val arguments = SleepQualityFragmentArgs.fromBundle(arguments!!)

        // calling viewModelFactory to prepare a viewModel containing application and data source
        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey,dataSource)

        // getting a viewModel from viewModelFactory
        val sleepQualityViewModel = ViewModelProviders.of(this,viewModelFactory).get(SleepQualityViewModel::class.java)

        // adding viewModel to data binding ( sleepQualityViewModel variable added in layout file )
        binding.sleepQualityViewModel = sleepQualityViewModel

        // navigating from sleepQuality to sleepTracker
        sleepQualityViewModel.navigateToSleepTracker.observe(viewLifecycleOwner,  Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                // to confirm that navigation is done
                sleepQualityViewModel.doneNavigating()
            }
        })

        return binding.root
    }
}
