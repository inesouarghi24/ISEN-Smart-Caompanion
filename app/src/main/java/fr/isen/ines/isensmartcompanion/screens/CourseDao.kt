package fr.isen.ines.isensmartcompanion.screens

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE date = :selectedDate")
    fun getCoursesByDate(selectedDate: String): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)
}

