package uz.orign.todoappmvc.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.orign.todoappmvc.R
import uz.orign.todoappmvc.databinding.CategoryItemLayoutBinding
import uz.orign.todoappmvc.room.entity.Category

/**
 *Created by Xushnudbek Tursunboyev on 09/10/2022
 */

class CategoryAdapter(val onClick: (model: Category) -> Unit) : ListAdapter<Category, CategoryAdapter.ViewHolder>(ITEM_DIFF) {

    companion object {
        private val ITEM_DIFF = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.category_id == newItem.category_id

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem.category_name == newItem.category_name && oldItem.category_color == newItem.category_color && oldItem.taskCount == newItem.taskCount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryItemLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: CategoryItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(d: Category) {
            binding.apply {
                root.setOnClickListener {
                    onClick.invoke(d)

                }
                if (d.category_color == R.color.yellow_c || d.category_color == R.color.grey_c) {
                    tvCtName.setTextColor(R.color.black)
                    tvCtTaskCount.setTextColor(R.color.black)
                }
                if (d.taskCount <= 1) {
                    tvCtTaskCount.text = d.taskCount.toString() + " task"
                } else
                    tvCtTaskCount.text = d.taskCount.toString() + " tasks"
                tvCtName.text = d.category_name
                ln.setBackgroundResource(d.category_color)
            }
        }
    }
}