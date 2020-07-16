package com.project.timeplanner.ui

import android.graphics.Color
import android.os.Bundle
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
import com.project.timeplanner.R
import com.project.timeplanner.TaskListAdapter
import com.project.timeplanner.datamodels.TaskViewModel
import com.project.timeplanner.databinding.FragmentMainBinding
import com.project.timeplanner.Utils.SwipeToDeleteCallback
import com.project.timeplanner.databinding.FragmentNewToDoBinding
import com.project.timeplanner.databinding.FragmentSecondayBinding

class SecondaryFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    lateinit var binding: FragmentSecondayBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_seconday, container, false
        )

        val wordListAdapter = TaskListAdapter(context, binding.fab, 1)
        binding.recyclerview.adapter = wordListAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        taskViewModel = TaskViewModel(activity!!.application, "None")

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
                snackbar.setAction(R.string.undo) {
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


}

