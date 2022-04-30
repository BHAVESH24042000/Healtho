package com.example.healtho.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healtho.R
import com.example.healtho.databinding.ItemExerciseStatusBinding
import com.example.healtho.databinding.WaterListItemBinding
import com.example.healtho.homescreens.models.ExerciseModel
import com.example.healtho.homescreens.models.Sleep
import com.example.healtho.homescreens.models.Water
import com.example.healtho.util.getFormattedTime
import javax.inject.Inject


class ExerciseStatusAdapter@Inject constructor(private val applicationContext: Context): ListAdapter<ExerciseModel, ExerciseStatusAdapter.ViewHolder>(DiffCall()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemExerciseStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private var binding: ItemExerciseStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ExerciseModel) {

            binding.itemTv.text = model.id.toString()

            if(model.isSelected){
                binding.itemTv.background = ContextCompat.getDrawable(applicationContext,R.drawable.item_circular_thin_color_border)
                binding.itemTv.setTextColor(Color.parseColor("#212121"))
            }else if(model.isCompleted){
                binding.itemTv.background = ContextCompat.getDrawable(applicationContext,R.drawable.circular_color_background)
                binding.itemTv.setTextColor(Color.parseColor("#FFFFFF"))
            }else{
                binding.itemTv.background = ContextCompat.getDrawable(applicationContext,R.drawable.item_circular_color_gray_bg)
                binding.itemTv.setTextColor(Color.parseColor("#212121"))
            }
        }
    }


    class DiffCall : DiffUtil.ItemCallback<ExerciseModel>() {
        override fun areItemsTheSame(
            oldItem: ExerciseModel,
            newItem: ExerciseModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ExerciseModel,
            newItem: ExerciseModel
        ): Boolean {
            return oldItem == newItem
        }
    }

}