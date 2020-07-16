package com.project.timeplanner.datamodels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks_table")
data class Task(
    @ColumnInfo(name = "task") val task: String, @ColumnInfo(name = "time") val time: Long, @ColumnInfo(
        name = "tag") val tag: String, @ColumnInfo(name = "date") val date: String, @PrimaryKey @ColumnInfo(name = "id") val id: Long
)


