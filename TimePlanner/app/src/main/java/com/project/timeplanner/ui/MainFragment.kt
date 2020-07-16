package com.project.timeplanner.ui


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.timeplanner.MainActivity
import com.project.timeplanner.R
import com.project.timeplanner.TaskListAdapter
import com.project.timeplanner.datamodels.TaskViewModel
import com.project.timeplanner.databinding.FragmentMainBinding
import com.project.timeplanner.Utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.text.SimpleDateFormat
import java.util.*






class MainFragment : Fragment() {



    private lateinit var taskViewModel: TaskViewModel
    lateinit var binding: FragmentMainBinding
    val formatter = SimpleDateFormat("d MMM yyyy", Locale.US)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main, container, false
        )
        val main = activity as MainActivity
        if (main.date=="") {
            binding.parent.day_field.text = getToday()
            main.date = getToday()
        }
        else
            binding.parent.day_field.text = main.date

        val wordListAdapter = TaskListAdapter(context, binding.fab, 0)
        binding.recyclerview.adapter = wordListAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        binding.parent.leftarrow.setOnClickListener {
            binding.parent.day_field.text = changeDate(binding.parent.day_field.text.toString(), true)
            main.date = binding.parent.day_field.text.toString()
            wordListAdapter.notifyDataSetChanged()
            taskViewModel = TaskViewModel(activity!!.application, binding.parent.day_field.text.toString())

            taskViewModel.allTasks.observe(this, Observer { words ->
                // Update the cached copy of the words in the adapter.
                if (words.isEmpty()) {
                    binding.emptyPh.visibility = View.VISIBLE
                } else {
                    binding.emptyPh.visibility = View.INVISIBLE
                }
                words?.let { wordListAdapter.setTasks(it, context, taskViewModel, binding.recyclerview) }
            })
        }

        binding.parent.rightarrow.setOnClickListener {
            binding.parent.day_field.text = changeDate(binding.parent.day_field.text.toString(), false)
            main.date = binding.parent.day_field.text.toString()
            wordListAdapter.notifyDataSetChanged()
            taskViewModel = TaskViewModel(activity!!.application, binding.parent.day_field.text.toString())

            taskViewModel.allTasks.observe(this, Observer { words ->
                // Update the cached copy of the words in the adapter.
                if (words.isEmpty()) {
                    binding.emptyPh.visibility = View.VISIBLE
                } else {
                    binding.emptyPh.visibility = View.INVISIBLE
                }
                words?.let { wordListAdapter.setTasks(it, context, taskViewModel, binding.recyclerview) }
            })
        }

        taskViewModel = TaskViewModel(activity!!.application, binding.parent.day_field.text.toString())

        taskViewModel.allTasks.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            if (words.isEmpty()) {
                binding.emptyPh.visibility = View.VISIBLE
            } else {
                binding.emptyPh.visibility = View.INVISIBLE
            }
            words?.let { wordListAdapter.setTasks(it, context, taskViewModel, binding.recyclerview) }
        })

        binding.fab.setOnClickListener {
            it.findNavController().navigate(R.id.NewToDoTask)
        }
        enableSwipeToDeleteAndUndo(wordListAdapter)

        return binding.root
    }
    private fun enableSwipeToDeleteAndUndo(wordListAdapter: TaskListAdapter) {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {


                val position = viewHolder.adapterPosition
                val item = wordListAdapter.getList()[position]

                wordListAdapter.removeItem(position)


                val snackbar = Snackbar
                    .make(binding.coodLayout, getString(R.string.removedfromlist), Snackbar.LENGTH_LONG)
                snackbar.setAction(getString(R.string.undo)) {
                    wordListAdapter.restoreItem(item, position)
                    binding.recyclerview.scrollToPosition(position)
                }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()

            }
        }

        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(binding.recyclerview)
    }
    private fun changeDate(currenttime: String, left: Boolean): String {

        val date = formatter.parse(currenttime)
        val c = Calendar.getInstance()
        c.time = date
        if (left)
            c.add(Calendar.DATE, -1)
        else
            c.add(Calendar.DATE, 1)
        return formatter.format(c.time)

    }

    private fun getToday() : String {
        return formatter.format(Date(System.currentTimeMillis()))
    }




}
