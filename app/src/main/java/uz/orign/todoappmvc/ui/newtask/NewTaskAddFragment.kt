package uz.orign.todoappmvc.ui.newtask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.orign.todoappmvc.R
import uz.orign.todoappmvc.adapter.CategoryAdapterNew
import uz.orign.todoappmvc.broadcast.AlarmController
import uz.orign.todoappmvc.databinding.CategoryItemLayoutBinding
import uz.orign.todoappmvc.databinding.FragmentNewTaskAddBinding
import uz.orign.todoappmvc.databinding.SelectCategoryItemBinding
import uz.orign.todoappmvc.room.RoomDbHelper
import uz.orign.todoappmvc.room.entity.Category
import uz.orign.todoappmvc.room.entity.TaskModel
import uz.orign.todoappmvc.ui.global.BaseFragment


class NewTaskAddFragment : BaseFragment(R.layout.fragment_new_task_add) {

    private var _bn: FragmentNewTaskAddBinding? = null
    private val bn get() = _bn ?: throw NullPointerException("cannot inflate")
    private val navController: NavController by lazy(LazyThreadSafetyMode.NONE) { NavHostFragment.findNavController(this) }
    private var date = ""
    private var time = ""
    private var isCategorySelect = false
    private var isclick = false
    private var categoryID = 0
    private var ctColor = 0
    private lateinit var alarmController: AlarmController


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bn = FragmentNewTaskAddBinding.bind(view)
        setUpUI()
    }


    override fun setUpUI() {
        alarmController = AlarmController(requireContext())

        bn.apply {
            tvCancel.setOnClickListener {
                navController.popBackStack()
            }
            tvDone.setOnClickListener {
                saveTask()
            }

            btnCalendar.setOnClickListener {
                showCalendarDialog()
            }
            btnAlarm.setOnClickListener {
                showAlarmDialog()
            }
            tvCategory.setOnClickListener {
                showCategoryRv()
            }
            radioBtn.setOnClickListener {
                isclick = !isclick
                if (isclick) {
                    bn.radioBtn.setImageResource(R.drawable.ic_marked)
                } else {
                    bn.radioBtn.setImageResource(R.drawable.ic_unmarked)
                }
            }
        }


    }

    private fun showCategoryRv() {
        val dialog = BottomSheetDialog(requireActivity())
        val dialogView = SelectCategoryItemBinding.inflate(layoutInflater, bn.root, false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogView.root)

        var tasCount = ArrayList<Int>()
        var isCategory: Category? = null
        var categoryAdapter = CategoryAdapterNew()

        var list = RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).categoryDao().getCategory()

        list.forEach {
            tasCount.add(RoomDbHelper.DatabaseBuilder.getInstance(requireActivity()).taskDao().getTasksByCategory(it.category_id).size)
        }

        categoryAdapter.context = requireActivity()
        categoryAdapter.tascount = tasCount
        categoryAdapter.list = list
        categoryAdapter.isTask = true
        categoryAdapter.onpress = object : CategoryAdapterNew.onPress {
            override fun selected(
                position: Int,
                oldItem: Int,
                list: ArrayList<CategoryItemLayoutBinding>,
                itemview: CategoryItemLayoutBinding,
                category: Category
            ) {
                bn.categoryColor2.setBackgroundColor(ContextCompat.getColor(requireActivity(), category.category_color!!))
                bn.tvCategory.text = category.category_name
                isCategory = category
                categoryID = category.category_id
                isCategorySelect = true
                ctColor = category.category_color
            }

            override fun click(category: Category) {

            }
        }
        dialogView.rvCategory.adapter = categoryAdapter
        categoryAdapter.notifyDataSetChanged()
        dialog.show()
    }

    private fun showAlarmDialog() {
        if (date.isNotEmpty()) {
            var timepicker = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    val hour1 = String.format("%02d", hourOfDay)
                    val minute1 = String.format("%02d", minute)
                    time = "$hour1:$minute1"
                    bn.alarm1.visibility = View.VISIBLE
                    bn.alarmTime.visibility = View.VISIBLE
                    bn.alarmTime.text = time
                }, 24, 60, true
            )
            timepicker.show()
        } else Toast.makeText(requireActivity(), "please add date", Toast.LENGTH_SHORT).show()
    }

    private fun showCalendarDialog() {

        var datatimepicker = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DatePickerDialog(requireContext())
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        datatimepicker.show()
        datatimepicker.setOnDateSetListener { view, year, month, dayOfMonth ->
            val mont = String.format("%02d", month + 1)
            val day = String.format("%02d", dayOfMonth)
            date = "$day.$mont.$year"
            bn.calendar1.visibility = View.VISIBLE
            bn.calendarDate.visibility = View.VISIBLE
            bn.calendarDate.text = date
        }
    }

    private fun saveTask() {
        try {
            if (isCategorySelect) {
                val taskModel = TaskModel(0, bn.tvTitle.text.toString(), isclick, categoryID, date, time, ctColor)
                RoomDbHelper.DatabaseBuilder.getInstance(requireContext()).taskDao().addTask(taskModel)
                Toast.makeText(requireActivity(), "Save new task!", Toast.LENGTH_SHORT).show()
                alarmController.setAlarm("${time.substring(0, 2)}${time.substring(3)}".toInt(), date, time, bn.tvTitle.text.toString())
                navController.popBackStack()
            } else
                Toast.makeText(requireActivity(), "Please select category!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            navController.popBackStack()
        }
    }


}