package uz.orign.todoappmvc.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.orign.todoappmvc.room.dao.CategoryDao
import uz.orign.todoappmvc.room.dao.TaskDao
import uz.orign.todoappmvc.room.entity.Category
import uz.orign.todoappmvc.room.entity.TaskModel

@Database(
    entities = [TaskModel::class, Category::class],
    version = 1,
    exportSchema = true
)

abstract class RoomDbHelper : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    object DatabaseBuilder {

        private var instance: RoomDbHelper? = null

        fun getInstance(context: Context): RoomDbHelper {
            if (instance == null) {
                synchronized(this) {
                    instance = buildRoomDb(context)
                }
            }
            return instance!!
        }

        private fun buildRoomDb(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                RoomDbHelper::class.java,
                "room.todo.db"
            )
                .allowMainThreadQueries()
                .build()

    }
}