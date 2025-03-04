package fr.isen.ines.isensmartcompanion.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).courseDao()

    private val _courses = MutableStateFlow<List<CourseEntity>>(emptyList())
    val courses: StateFlow<List<CourseEntity>> = _courses.asStateFlow()

    fun fetchCourses(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _courses.value = dao.getCoursesByDate(date)
        }
    }



    fun addCourse(date: String, time: String, room: String, subject: String) {
        viewModelScope.launch {
            val course = CourseEntity(date = date, time = time, room = room, subject = subject)
            dao.insertCourse(course)
            fetchCourses(date)
        }
    }

    fun removeCourse(course: CourseEntity) {
        viewModelScope.launch {
            dao.deleteCourse(course)
            fetchCourses(course.date)
        }
    }
}
