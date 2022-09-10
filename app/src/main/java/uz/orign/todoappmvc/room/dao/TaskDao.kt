package uz.orign.todoappmvc.room.dao

import androidx.room.*
import uz.orign.todoappmvc.room.entity.Category
import uz.orign.todoappmvc.room.entity.TaskModel

@Dao
interface TaskDao {

    //task
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTask(taskModel: TaskModel):Long

    @Query("select * from tasks order by tasks.id")
    fun getTasks():List<TaskModel>

    @Delete
    fun delete(taskModel: TaskModel)

    @Update
    fun update(taskModel: TaskModel)

    @Query("select * from tasks where tasks.categoryID == :id")
    fun getTasksByCategory(id:Int):List<TaskModel>


}