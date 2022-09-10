package uz.orign.todoappmvc.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.orign.todoappmvc.R
import uz.orign.todoappmvc.databinding.TaskItemLayoutBinding
import uz.orign.todoappmvc.room.entity.TaskModel

/**
 *Created by Xushnudbek Tursunboyev on 09/10/2022
 */


class TasksAdapter(val onClick: (taskModel: TaskModel) -> Unit) : ListAdapter<TaskModel, TasksAdapter.ViewHolder>(ITEM_DIFF) {

    companion object {
        private val ITEM_DIFF = object : DiffUtil.ItemCallback<TaskModel>() {
            override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean =
                oldItem.taskName == newItem.taskName && oldItem.taskComplete == newItem.taskComplete && oldItem.categoryID == newItem.categoryID && oldItem.date==
                        newItem.date && oldItem.time == newItem.time && oldItem.categoryColor == newItem.categoryColor
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TaskItemLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: TaskItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(d: TaskModel) {
            binding.apply {
                root.setOnClickListener {
                    onClick.invoke(d)
                }

                categoryColor.setBackgroundResource(d.categoryColor)


                if (d.taskComplete){
                    select.setImageResource(R.drawable.ic_marked)
                }else{
                    select.setImageResource(R.drawable.ic_unmarked)
                }
                tvName.text = d.taskName
                calendarDate.text = d.date
                alarmTime.text = d.time
            }
        }
    }
}