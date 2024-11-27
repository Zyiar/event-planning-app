package com.example.eventplanningapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.eventplanningapp.database.Task
import com.example.eventplanningapp.database.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope


class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    // Exposing all tasks as a Flow
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()


    // Function to add a new task
    fun addTask(task: Task) {
        viewModelScope.launch {
            Log.d("TaskViewModel", "Adding task: $task")
            taskDao.insert(task)
        }
    }

    // Function to update task completion status
    fun updateTaskCompletion(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.update(task)
        }
    }


    // Function to delete a task
    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.delete(task)
        }
    }

}

// Factory to instantiate TaskViewModel with TaskDao
class TaskViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Ensure the ViewModel class matches and the TaskDao is correctly passed
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
