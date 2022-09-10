package uz.orign.todoappmvc.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import uz.orign.todoappmvc.R
import uz.orign.todoappmvc.adapter.TasksAdapterNew
import uz.orign.todoappmvc.databinding.EditDialogBinding
import uz.orign.todoappmvc.databinding.FragmentEditTaskBinding
import uz.orign.todoappmvc.databinding.FragmentHomeBinding
import uz.orign.todoappmvc.room.RoomDbHelper
import uz.orign.todoappmvc.room.entity.Category
import uz.orign.todoappmvc.room.entity.TaskModel
import uz.orign.todoappmvc.ui.global.BaseFragment


class EditTaskFragment : BaseFragment(R.layout.fragment_edit_task) {

    private var _bn: FragmentEditTaskBinding? = null
    private val bn get() = _bn ?: throw NullPointerException("cannot inflate")
    private var categoryID = 0
    private val navController: NavController by lazy(LazyThreadSafetyMode.NONE) { NavHostFragment.findNavController(this) }
    private var adapterTasks = TasksAdapterNew {
        var checked = !it.taskComplete
        var taskModel = TaskModel(it.id, it.taskName, checked, it.categoryID, it.date, it.time, it.categoryColor)
        RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).taskDao().update(taskModel)
        loadTasks()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryID =it.getInt("category_ID")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bn = FragmentEditTaskBinding.bind(view)
        setUpUI()
    }

    @SuppressLint("ResourceAsColor")
    override fun setUpUI() {
        loadTasks()
        bn.apply {
           // constraint.setBackgroundResource(RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().getCategoryByID(categoryID) .category_color)
            constraint2.setBackgroundResource(RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().getCategoryByID(categoryID)
                .category_color)
            val category = RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().getCategoryByID(categoryID)
            btnEdit.setBackgroundResource(category.category_color)
            btnEdit.setOnClickListener {
                editCategory(category)
            }
            tvCategory.text = category.category_name
            tvTasksCount.text = category.taskCount.toString()
            if (category.category_color == R.color.yellow_c || category.category_color == R.color.grey_c){
                tvCategory.setTextColor(R.color.black)
                tvTasksCount.setTextColor(R.color.black)
            }else{

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadTasks() {
        if (RoomDbHelper.DatabaseBuilder.getInstance(requireContext()).taskDao().getTasks().isNotEmpty()){
            adapterTasks.submitList(RoomDbHelper.DatabaseBuilder.getInstance(requireContext()).taskDao().getTasksByCategory(categoryID))
            bn.rvTasks.adapter = adapterTasks
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun editCategory(category: Category) {
        try {
            val dialog = Dialog(requireContext())
            var color = MutableLiveData<Int>()
            color.value = category.category_color
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            var view = EditDialogBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            view.edit.setText(category.category_name)
            if (category.category_color == R.color.yellow_c || category.category_color == R.color.grey_c) {
                view.add.setTextColor(R.color.grey_c)
            }
            try {
                color.observe(viewLifecycleOwner) {
                    view.add.setBackgroundColor(
                        requireContext().resources.getColor(it)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val childcount: Int = view.viewgroup.childCount
            for (i in 0 until childcount) {
                view.viewgroup.getChildAt(i).setOnClickListener {
                    when (it.id) {
                        R.id.black -> {
                            color.value = R.color.black
                        }
                        R.id.blue -> {
                            color.value = R.color.purple_c
                        }
                        R.id.purple -> {
                            color.value = R.color.purple_500
                        }
                        R.id.yellow -> {
                            color.value = R.color.yellow_c
                        }
                        R.id.red -> {
                            color.value = R.color.red_c
                        }
                        R.id.green -> {
                            color.value = R.color.green_c
                        }
                        R.id.grey -> {
                            color.value = R.color.grey_c
                        }
                    }
                }
            }
            color.observe(viewLifecycleOwner) { color->
            view.add.setOnClickListener {
                var taskCount = RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).taskDao().getTasksByCategory(category.category_id).size

                RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao()
                    .updateCategory(Category(category.category_id, view.edit.text.toString(), color, taskCount))

                bn.constraint.setBackgroundColor(
                    requireContext().resources.getColor(color)
                )
                bn.tvCategory.text = view.edit.text.toString()
                dialog.cancel()
            }
        }


            dialog.setContentView(view.root)
            dialog.setCancelable(true)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}