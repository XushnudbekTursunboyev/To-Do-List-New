package uz.orign.todoappmvc.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import uz.orign.todoappmvc.R
import uz.orign.todoappmvc.adapter.CategoryAdapter
import uz.orign.todoappmvc.adapter.TasksAdapter
import uz.orign.todoappmvc.databinding.EditDialogBinding
import uz.orign.todoappmvc.databinding.FragmentHomeBinding
import uz.orign.todoappmvc.room.RoomDbHelper
import uz.orign.todoappmvc.room.entity.Category
import uz.orign.todoappmvc.room.entity.TaskModel
import uz.orign.todoappmvc.ui.global.BaseFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 *Created by Xushnudbek Tursunboyev on 09/10/2022
 */


class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private var _bn: FragmentHomeBinding? = null
    private val bn get() = _bn ?: throw NullPointerException("cannot inflate")
    private lateinit var list: ArrayList<Category>
    private var adapter = CategoryAdapter {
        navController.navigate(R.id.editTaskFragment, bundleOf("category_ID" to it.category_id))
    }

    private var adapterTasks = TasksAdapter {
        var checked = !it.taskComplete
        var taskModel = TaskModel(it.id, it.taskName, checked, it.categoryID, it.date, it.time, it.categoryColor)
        RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).taskDao().update(taskModel)
        loadUIData()
    }

    private val navController: NavController by lazy(LazyThreadSafetyMode.NONE) { NavHostFragment.findNavController(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bn = FragmentHomeBinding.bind(view)
        setUpUI()
    }

    override fun setUpUI() {
        loadUIData()
        bn.apply {
            var dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.popup)
            var isclick = false
            fab.setOnClickListener {
                isclick = !isclick
                if (isclick) {
                    dilalog.visibility = View.VISIBLE
                    innerContainer.alpha = 0.2f
                    fab.rotation = 45f
                } else {
                    dilalog.visibility = View.GONE
                    innerContainer.alpha = 1f
                    fab.rotation = 0f
                }
            }

            addTask.setOnClickListener {
                findNavController().navigate(R.id.newTaskAddFragment)
            }

            addCategory.setOnClickListener {
                val dialog = Dialog(requireActivity())
                var color = MutableLiveData<Int>()
                color.value = R.color.black
                dialog.setCancelable(false)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                var view = EditDialogBinding.inflate(
                    LayoutInflater.from(requireActivity()),
                    null,
                    false
                )
                color.observe(viewLifecycleOwner) {
                    view.add.setBackgroundColor(
                        ContextCompat.getColor(
                            requireActivity(), it
                        )
                    )
                }
                val childcount: Int = view.viewgroup.childCount
                for (i in 0 until childcount) {
                    view.viewgroup.getChildAt(i).setOnClickListener {
                        when (it.id) {
                            R.id.black -> {
                                color.value = R.color.black
//                        view.add.setBackgroundColor(R.color.black)
                            }
                            R.id.blue -> {
                                color.value = R.color.purple_c
//                        view.add.setBackgroundColor(R.color.blue)
                            }
                            R.id.purple -> {
                                color.value = R.color.purple_500
//                        view.add.setBackgroundColor(R.color.purple)
                            }
                            R.id.yellow -> {
                                color.value = R.color.yellow_c
//                        view.add.setBackgroundColor(R.color.yellow)
                            }
                            R.id.red -> {
                                color.value = R.color.red_c
//                        view.add.setBackgroundColor(R.color.red)
                            }
                            R.id.green -> {
                                color.value = R.color.green_c
//                        view.add.setBackgroundColor(R.color.green)
                            }
                            R.id.grey -> {
                                color.value = R.color.grey_c
//                        view.add.setBackgroundColor(R.color.light_grey)
                            }
                        }
                    }
                }
                color.observe(viewLifecycleOwner) { color ->
                    view.add.setOnClickListener {
                        RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().addCategory(
                            Category(
                                0, view.edit.text.toString(), color, 0
                            )
                        )
                        dialog.dismiss()
                        dilalog.visibility = View.GONE
                        innerContainer.alpha = 1f
                        fab.rotation = 0f
                        loadUIData()
                    }
                }
                dialog.setContentView(view.root)
                dialog.setCancelable(true)
                dialog.show()
            }

        }
    }

    private fun loadUIData() {
        loadTasks()
        loadCategory()
    }

    @SuppressLint("SetTextI18n")
    private fun loadTasks() {
        if (RoomDbHelper.DatabaseBuilder.getInstance(requireContext()).taskDao().getTasks().isNotEmpty()) {
            val list = RoomDbHelper.DatabaseBuilder.getInstance(requireContext()).taskDao().getTasks()
            val todayList = ArrayList<TaskModel>()
            val df = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            val date1 = df.format(Calendar.getInstance().time)
            list.forEach {
                if (it.date == date1) todayList.add(it)
            }

            adapterTasks.submitList(todayList)
            bn.rvTasks.visibility = View.VISIBLE
            bn.tvEmpty.visibility = View.INVISIBLE
            bn.rvTasks.adapter = adapterTasks
        } else {
            bn.rvTasks.visibility = View.GONE
            bn.tvEmpty.visibility = View.VISIBLE
            bn.tvEmpty.text = "0 task"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bn.rvCategory.adapter = null
        bn.rvTasks.adapter = null
    }

    private fun loadCategory() {
        if (RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().getCategory().isEmpty()) {
            RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().addCategory(Category(0, "Inbox", R.color.grey_c, 0))
            RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().addCategory(Category(0, "Work", R.color.green_c, 0))
            RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().addCategory(Category(0, "Shopping", R.color.red_c, 0))
            RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().addCategory(Category(0, "Family", R.color.yellow_c, 0))
            RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().addCategory(Category(0, "Personal", R.color.purple_c, 0))
        }
        var listCt = RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().getCategory()
        listCt.forEach {
            var taskCountCt = RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).taskDao().getTasksByCategory(it.category_id).size
            Log.d("MyLog", taskCountCt.toString())
            val category =
                Category(category_id = it.category_id, category_name = it.category_name, category_color = it.category_color, taskCount = taskCountCt)
            RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().updateCategory(category)
        }
        adapter.submitList(RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().getCategory())
        bn.rvCategory.adapter = adapter
    }
}