package uz.orign.todoappmvc.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var category_id:Int = 0,
    var category_name:String,
    var category_color:Int,
    var taskCount:Int
)