package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar


// A fragment with buttons to record start and end times for sleep, which are saved in
// a database. Cumulative data is displayed in a simple scrollable TextView.
class SleepTrackerFragment : Fragment() {
    // Called when the Fragment is ready to display content to the screen
    // This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        // variable to get access to resources such as strings and styles
        val application = requireNotNull(this.activity).application

        // from instance of database getting all data in it
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // calling viewModelFactory to prepare a viewModel containing application and data source
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource,application)

        // getting the viewModel from viewModelFactory
        val sleepTrackerViewModel = ViewModelProviders.of(this,viewModelFactory)
            .get(SleepTrackerViewModel::class.java)

        // adding viewModel to data binding ( sleepTrackerViewModel variable added in layout file )
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        // adding viewModel to live data
        binding.lifecycleOwner = this

        // navigation to sleepQuality fragment
        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer{ night->
            // if not null then this will work
            night?.let{
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                )
                // called to again reassign it to null
                sleepTrackerViewModel.doneNavigating()
            }
        })

        // showing snack Bar event
        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                // confirming that the snack bar even is done
                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })

        // to tell recycler view about the adapter
        // val adapter = SleepNightAdapter()
//         val adapter = SleepNightAdapter(SleepNightListener { nightId ->
//             Toast.makeText(context, "${nightId}", Toast.LENGTH_SHORT).show()
//         })
        val adapter = SleepNightAdapter(SleepNightListener {
                nightId ->  sleepTrackerViewModel.onSleepNightClicked(nightId)
        })
        binding.sleepList.adapter = adapter

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner,Observer{
            it?.let{
                // adapter.data = it
                adapter.submitList(it)
            }
        })

        sleepTrackerViewModel.navigateToSleepDataQuality.observe(viewLifecycleOwner, Observer {night ->
            night?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections
                    .actionSleepTrackerFragmentToSleepDetailFragment(night))
                sleepTrackerViewModel.onSleepDataQualityNavigated()
            }
        })

        // converting the layout manager of our list to grid of span of 3
        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = manager

        return binding.root
    }
}
