package com.habit.tracker.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.habit.tracker.Enums.Frequency
import com.habit.tracker.Interface.OnItemClick
import com.habit.tracker.Model.Habit
import com.habit.tracker.databinding.ItemHabitBinding

class HabitAdapter(var context: Context, var onItemClick: OnItemClick) :
    RecyclerView.Adapter<HabitAdapter.ViewHolder>() {
    private var listData: List<Habit> = ArrayList()
    fun setData(list: List<Habit>) {
        this.listData=list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val binding=
            ItemHabitBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val habit=listData[i]
        with(holder.binding)
        {
            when (habit.count) {
                -1 -> {
                    txtGoalCount.text="Loading.."
                }
                -2 -> {
                    txtGoalCount.text="Error.."
                }
                else -> {
                    txtGoalCount.text="${habit.count}/${habit.goal}"
                }
            }
            if (habit.color != 0)
                cardView.setCardBackgroundColor(habit.color)
            else
                cardView.setCardBackgroundColor(Color.WHITE)

            if(habit.reminder)
                imgNotification.visibility=View.VISIBLE
            else
                imgNotification.visibility=View.INVISIBLE

            txtGoalFrequency.text=Frequency.getSelectedFrequency(habit.frequency)
            txtGoalName.text=habit.name

            imgEdit.setOnClickListener {
                onItemClick.onEditClick(holder.adapterPosition)
            }
            imgDelete.setOnClickListener {
                onItemClick.onDeleteClick(holder.adapterPosition)
            }
            holder.itemView.setOnClickListener {
                onItemClick.onClick(holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(var binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root)
}