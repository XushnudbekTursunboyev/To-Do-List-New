package uz.orign.todoappmvc.room.dao

import androidx.room.*
import uz.orign.todoappmvc.room.entity.Category

@Dao
interface CategoryDao {
    //category
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCategory(category: Category):Long

    @Query("select * from category order by category.category_id")
    fun getCategory():List<Category>

    @Query("select * from category where category.category_id == :id")
    fun getCategoryByID(id:Int):Category

    @Update
    fun updateCategory(category: Category)

    @Update
    fun allListUpdate(list: List<Category>)
}