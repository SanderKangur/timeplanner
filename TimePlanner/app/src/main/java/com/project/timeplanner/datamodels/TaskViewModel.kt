package com.project.timeplanner.datamodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

class TaskViewModel(application: Application, Date: String) : AndroidViewModel(Application()) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    init {
        val wordsDao = TaskRoomDatabase.getDatabase(application, scope).taskDao()
        repository = TaskRepository(wordsDao, Date)
        if (Date == "None")
            allTasks = repository.allTasks()
        else
            allTasks = repository.TasksByDate(Date)
    }

    fun insert(task: Task) = scope.launch(Dispatchers.IO) {
        repository.insert(task)
    }

    fun delete(task: Task) = scope.launch(Dispatchers.IO) {
        repository.delete(task)
    }

    fun deleteAll() = scope.launch(Dispatchers.IO) {
        repository.clearData()
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}