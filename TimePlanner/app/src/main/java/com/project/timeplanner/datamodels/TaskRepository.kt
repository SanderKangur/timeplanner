package com.project.timeplanner.datamodels

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao, private val date: String) {

    @WorkerThread
    fun insert(task: Task) {
        taskDao.insert(task)
    }

    @WorkerThread
    fun allTasks(): LiveData<List<Task>>  {
        return taskDao.getAllTasks()
    }

    @WorkerThread
    fun TasksByDate(date: String): LiveData<List<Task>>  {
        return taskDao.getTasksOnDate(date)
    }

    @WorkerThread
    fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    @WorkerThread
    fun clearData() {
        taskDao.deleteAll()
    }
}