package com.project.timeplanner.datamodels

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert
    fun insert(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM tasks_table")
    fun deleteAll()

    @Query("SELECT * from tasks_table ORDER BY time ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * from tasks_table WHERE date = :date ORDER BY time ASC")
    fun getTasksOnDate(date: String): LiveData<List<Task>>
}