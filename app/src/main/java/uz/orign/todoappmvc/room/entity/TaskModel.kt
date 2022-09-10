package uz.orign.todoappmvc.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks")
class TaskModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var taskName:String,
    var taskComplete:Boolean,
    var categoryID:Int,
    var date:String,
    var time:String,
    var categoryColor:Int
) : Serializable
