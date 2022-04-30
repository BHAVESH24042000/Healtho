package com.example.healtho.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healtho.databinding.SleepListItemBinding
import com.example.healtho.homescreens.models.Sleep
import com.example.healtho.util.formatDuration
import com.example.healtho.util.getFormattedTime

class SleepLogAdapter :
    ListAdapter<Sleep, SleepLogAdapter.SleepViewHolder>(DiffCall()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
        val binding =
            SleepListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SleepViewHolder(binding)
    }

    override fun onBindViewHolder(holderSleep: SleepViewHolder, position: Int) {
        val item = getItem(position)
        holderSleep.bind(item)
    }

    inner class SleepViewHolder(private val binding: SleepListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Sleep) {
            binding.sleepText.text = data.sleepDuration?.formatDuration().toString()
            binding.sleepTime.text = data.timeStamp?.getFormattedTime()
        }
    }

    class DiffCall : DiffUtil.ItemCallback<Sleep>() {
        override fun areItemsTheSame(
            oldItem: Sleep,
            newItem: Sleep
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Sleep,
            newItem: Sleep
        ): Boolean {
            return oldItem == newItem
        }
    }
}
