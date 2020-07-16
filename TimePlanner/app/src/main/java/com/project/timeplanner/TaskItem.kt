package com.project.timeplanner


class TaskItem {
    companion object Factory {
        fun create(): TaskItem = TaskItem()
    }
//    var taskId: String? = null
    var description: String? = null
    var task: String? = null
    var done: Boolean? = false
}