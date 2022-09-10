package uz.orign.todoappmvc.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.DelicateCoroutinesApi
import uz.orign.todoappmvc.R
import uz.orign.todoappmvc.databinding.CategoryItemLayoutBinding
import uz.orign.todoappmvc.room.entity.Category


class CategoryAdapterNew() : RecyclerView.Adapter<CategoryAdapterNew.Vh>() {
    lateinit var context: Context
    lateinit var tascount: ArrayList<Int>
    lateinit var viewowner: Fragment
    lateinit var list: List<Category>
    var isTask: Boolean = false
    lateinit var onpress: onPress
    var oldItem = -1
    var itemViewList = ArrayList<CategoryItemLayoutBinding>()

    constructor(context: Context, tascount: ArrayList<Int>, list: List<Category>, isTask: Boolean = false, onpress: onPress) : this() {
        this.context = context
        this.tascount = tascount
        this.list = list
        this.isTask = isTask
        this.onpress = onpress
    }

    inner class Vh(var itemview: CategoryItemLayoutBinding) : RecyclerView.ViewHolder(itemview.root) {
        @OptIn(DelicateCoroutinesApi::class)
        @SuppressLint("ResourceAsColor", "NotifyDataSetChanged", "SetTextI18n")
        fun bind(category: Category, position: Int) {
//            setupViewModel()


            itemview.tvCtTaskCount.text = "${tascount[position]} task"


            if (category.category_color == R.color.yellow_c || category.category_color == R.color.grey_c) {
                itemview.tvCtName.setTextColor(R.color.grey_c)
                itemview.tvCtTaskCount.setTextColor(R.color.grey_c)
            }
            itemview.tvCtName.text = category.category_name
            itemview.ln.setBackgroundColor(context.resources.getColor(category.category_color!!))
            itemview.container.setOnClickListener {
                if (isTask) {
                    if (oldItem < 0) {
                        itemview.categorySelect.visibility = View.VISIBLE
                    } else {
                        itemViewList[oldItem].categorySelect.visibility = View.INVISIBLE
                        itemViewList[position].categorySelect.visibility = View.VISIBLE
                    }
                    onpress.selected(position, oldItem, itemViewList, itemview, category)
                    oldItem = position
                } else {
                    onpress.click(category)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        var vh = Vh(CategoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        itemViewList.add(vh.itemview)
        return vh
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(list[position], position)

    }

    override fun getItemCount(): Int = list.size

    interface onPress {
        fun selected(
            position: Int,
            oldItem: Int,
            list: ArrayList<CategoryItemLayoutBinding>,
            itemview: CategoryItemLayoutBinding,
            category: Category
        )

        fun click(category: Category)
    }

}