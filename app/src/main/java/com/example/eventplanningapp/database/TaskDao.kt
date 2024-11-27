package com.example.eventplanningapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.eventplanningapp.database.Task

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task_table")
    fun getAllTasks(): LiveData<List<Task>>
}
