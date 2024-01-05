package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.GridItemSleepNightBinding

// By using an adapter, the details of recycling, scrolling and how to display info do not require
// any changes to our viewModel or room database

class SleepNightAdapter(val clickListener: SleepNightListener) : ListAdapter<SleepNight,
        SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()){

// SleepNightAdapter class extend RecyclerView.Adapter and hold some kind of ViewHolder
//class SleepNightAdapter : RecyclerView.Adapter<SleepNightAdapter.ViewHolder>(){
    // list of nights is defined as data
    // var data = listOf<SleepNight>()

    // This tell the recycler view that the entire dataset has changed and we will call
    // recycler view to immediately redraw everything on screen based on new data.
    // set(value) {
        // field = value
        // notifyDataSetChanged()
    // }

    // Three Important Functions to write in Adapter
    // 1. How many Items?
    // override fun getItemCount() = data.size

    // 2. How to draw an item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // val item = data[position]
        // holder.textView.text = item.sleepQuality.toString()

        // val res = holder.itemView.context.resources
        // holder.sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
        // holder.quality.text = convertNumericQualityToString(item.sleepQuality, res)
        // holder.qualityImage.setImageResource(when (item.sleepQuality) {
            // 0 -> R.drawable.ic_sleep_0
            // 1 -> R.drawable.ic_sleep_1
            // 2 -> R.drawable.ic_sleep_2
            // 3 -> R.drawable.ic_sleep_3
            // 4 -> R.drawable.ic_sleep_4
            // 5 -> R.drawable.ic_sleep_5
            // else -> R.drawable.ic_sleep_active
        // })

        // val item = getItem(position)
        // holder.bind(item)

        holder.bind(getItem(position)!!,clickListener)
    }

    // 3. Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // val layoutInflater = LayoutInflater.from(parent.context)
        // val view = layoutInflater .inflate(R.layout.text_item_view, parent, false) as TextView
        // return TextItemViewHolder(view)

        // return ViewHolder(view)

        return ViewHolder.from(parent)
    }

    // we create a class of viewHolder that contains information about our views
    class ViewHolder private constructor(val binding: GridItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root){
        // val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
        // val quality: TextView = itemView.findViewById(R.id.quality_string)
        // val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)

        fun bind(item: SleepNight, clickListener: SleepNightListener) {
//            val res = itemView.context.resources
//            binding.sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
//            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
//            binding.qualityImage.setImageResource(
//                when (item.sleepQuality) {
//                    0 -> R.drawable.ic_sleep_0
//                    1 -> R.drawable.ic_sleep_1
//                    2 -> R.drawable.ic_sleep_2
//                    3 -> R.drawable.ic_sleep_3
//                    4 -> R.drawable.ic_sleep_4
//                    5 -> R.drawable.ic_sleep_5
//                    else -> R.drawable.ic_sleep_active
//                }
//            )

            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                //val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
                //return ViewHolder(view)
                val binding = GridItemSleepNightBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }
    }

}


// Callback for calculating the diff between two non-null items in a list.
// Used by ListAdapter to calculate the minimum number of changes between and old list and a new
// list that's been passed to `submitList`.
class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>(){
    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}

class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}
