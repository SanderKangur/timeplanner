package com.project.timeplanner

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.github.abdularis.civ.AvatarImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.timeplanner.datamodels.Task
import com.project.timeplanner.datamodels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class TaskListAdapter internal constructor(
    context: Context?, v: FloatingActionButton
, frag: Int) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private val frag = frag
    private val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.US)
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var tasks = ArrayList<Task>() // Cached copy of words
    private lateinit var ctx: Context
    private lateinit var view: View
    private val parentView: FloatingActionButton = v
    private lateinit var taskViewModel: TaskViewModel
    private val colors: IntArray = intArrayOf(
        Color.rgb(244, 81, 30),
        Color.rgb(17, 94, 231),
        Color.rgb(9, 187, 69),
        Color.rgb(123, 31, 162),
        Color.rgb(191, 27, 19),
        Color.rgb(0, 121, 107),
        Color.rgb(255, 143, 0),
        Color.rgb(216, 27, 96)
    )

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskItemView: TextView = itemView.findViewById(R.id.task)
        val timeItemView: TextView = itemView.findViewById(R.id.time)
        val avImageView: AvatarImageView = itemView.findViewById(R.id.TxtImg)
        val relcard: RelativeLayout = itemView.findViewById(R.id.relcard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = inflater.inflate(R.layout.row_layout, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = tasks[position]
        holder.taskItemView.text = current.task
        holder.timeItemView.text = formatter.format(Date(current.time))
        holder.avImageView.setText(current.task.toCharArray()[0] + "")
        holder.avImageView.avatarBackgroundColor = colors[Random.nextInt(0, 8)]
        holder.relcard.setOnClickListener {
            val color = Color.argb(
                255,
                Random.nextInt(256),
                Random.nextInt(256),
                Random.nextInt(256)
            )

            holder.taskItemView.setTextColor(color)

            val formatter = SimpleDateFormat("d/MM/yyyy HH:mm")
            val time = formatter.format(current.time)

            var bundle = bundleOf("title" to current.task, "time" to time, "reminder" to current.tag)
            Log.i("time", time)
            Log.i("reminder", current.tag)
            if(frag == 0)
                it.findNavController().navigate(R.id.action_navigation_home_to_NewToDoTask, bundle)
            else
                it.findNavController().navigate(R.id.action_navigation_dashboard_to_NewToDoTask, bundle)

        }
    }

    internal fun setTasks(tasks: List<Task>, ctx: Context?, taskViewModel: TaskViewModel, view: View) {
        this.tasks = tasks as ArrayList<Task>
        this.ctx = ctx!!
        this.taskViewModel = taskViewModel
        this.view = view
        notifyDataSetChanged()
    }

    override fun getItemCount() = tasks.size

    fun getList() = tasks

    fun removeItem(position: Int) {
        taskViewModel.delete(tasks[position])
        WorkManager.getInstance().cancelAllWorkByTag(tasks[position].tag)
        notifyItemRemoved(position)
    }

    fun restoreItem(task: Task, position: Int) {
        tasks.add(position, task)
        notifyItemChanged(position)
        taskViewModel.insert(task)
    }


}