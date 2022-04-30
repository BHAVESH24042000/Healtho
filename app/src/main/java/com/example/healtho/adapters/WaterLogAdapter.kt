package com.example.healtho.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healtho.databinding.WaterListItemBinding
import com.example.healtho.homescreens.models.Water
import com.example.healtho.util.getFormattedTime


class WaterLogAdapter :
    ListAdapter<Water, WaterLogAdapter.ViewHolder>(DiffCall()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            WaterListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private var binding: WaterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Water) {
            binding.waterText.text = data.quantity
            binding.waterTime.text = data.timeStamp.getFormattedTime()
        }
    }

    class DiffCall : DiffUtil.ItemCallback<Water>() {
        override fun areItemsTheSame(
            oldItem: Water,
            newItem: Water
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Water,
            newItem: Water
        ): Boolean {
            return oldItem == newItem
        }
    }
}
