package com.project.timeplanner.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.project.timeplanner.R
import com.project.timeplanner.datamodels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.project.timeplanner.Utils.Notify
import com.project.timeplanner.databinding.FragmentNewToDoBinding
import com.project.timeplanner.datamodels.Task
import kotlinx.android.synthetic.main.fragment_new_to_do.*


class addTaskFragment : Fragment() {

    var date = Calendar.getInstance()

    var year = date.get(Calendar.YEAR)
    var month = date.get(Calendar.MONTH)
    var day = date.get(Calendar.DAY_OF_MONTH)
    var hr = date.get(Calendar.HOUR_OF_DAY)
    var min = date.get(Calendar.MINUTE)

    var checked: Boolean = false
    var yearR = 0
    var monthR = 0
    var dayR = 0
    var hrR = 0
    var minR = 0

    private lateinit var taskViewModel: TaskViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(arguments != null) {
            userToDoEditText.setText(arguments?.getString("title"))
            val date = arguments?.getString("time")?.split(" ")!![0]
            val time = arguments?.getString("time")?.split(" ")!![1]
            year = date.split("/")[2].toInt()
            month = date.split("/")[1].toInt()
            day = date.split("/")[0].toInt()
            hr = time.split(":")[0].toInt()
            min = time.split(":")[1].toInt()

            EnterTime.setText("$hr:$min")
            EnterDate.setText("$day/$month/$year")

            val reminder = arguments?.getString("reminder")
            if(reminder != "tag"){
                val formatter = SimpleDateFormat("d/MM/yyyy HH:mm")
                val datetimeR = formatter.format(reminder?.toLong())

                checked = true
                HasRemind.isChecked = true
                val dateR = datetimeR.split(" ")[0]
                val timeR = datetimeR.split(" ")[1]
                yearR = dateR.split("/")[2].toInt()
                monthR = dateR.split("/")[1].toInt()
                dayR = dateR.split("/")[0].toInt()
                hrR = timeR.split(":")[0].toInt()
                minR = timeR.split(":")[1].toInt()

                EnterTimeR.setText("$hrR:$minR")
                EnterDateR.setText("$dayR/$monthR/$yearR")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentNewToDoBinding>(
            inflater,
            R.layout.fragment_new_to_do, container, false
        )
        val appSharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(context?.applicationContext)
        taskViewModel = TaskViewModel(activity!!.application, "None")
        binding.HasRemind.setOnCheckedChangeListener {  _, isChecked ->
            if (!isChecked) {
                binding.EnterDateTimeR.visibility = View.INVISIBLE
                checked = false
            } else {
                binding.EnterDateTimeR.visibility = View.VISIBLE
                checked = true
            }
        }

        binding.EnterTime.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { _, i, i1 ->
                    binding.EnterTime.setText("$i:$i1")
                    hr = i
                    min = i1
                }, hour, minute, false
            )
            mTimePicker.show()
        }

        binding.EnterDate.setOnClickListener {
            val mcurrentDate = Calendar.getInstance()
            val mYear = mcurrentDate.get(Calendar.YEAR)
            val mMonth = mcurrentDate.get(Calendar.MONTH)
            val mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH)
            val myFormat = "dd MMM, yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val mDatePicker = DatePickerDialog(
                this.context!!,
                DatePickerDialog.OnDateSetListener { _, selectedyear, selectedmonth, selectedday ->
                    binding.EnterDate.setText(selectedday.toString() + "/" + (selectedmonth + 1) + "/" + selectedyear)
                    year = selectedyear
                    month = selectedmonth+1
                    day = selectedday
                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }

        binding.EnterTimeR.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { _, i, i1 ->
                    binding.EnterTimeR.setText("$i:$i1")
                    hrR = i
                    minR = i1
                }, hour, minute, false
            )
            mTimePicker.show()
        }

        binding.EnterDateR.setOnClickListener {
            val mcurrentDate = Calendar.getInstance()
            val mYear = mcurrentDate.get(Calendar.YEAR)
            val mMonth = mcurrentDate.get(Calendar.MONTH)
            val mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH)
            val myFormat = "dd MMM, yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val mDatePicker = DatePickerDialog(
                this.context!!,
                DatePickerDialog.OnDateSetListener { _, selectedyear, selectedmonth, selectedday ->
                    binding.EnterDateR.setText(selectedday.toString() + "/" + (selectedmonth + 1) + "/" + selectedyear)
                    yearR = selectedyear
                    monthR = selectedmonth+1
                    dayR = selectedday
                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }

        binding.makeToDoFloatingActionButton.setOnClickListener {
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.userToDoEditText.getWindowToken(), 0)

            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.userToDoEditText.text)) {
                val snackbar = Snackbar.make(
                    binding.ParentLayout,
                    getString(R.string.add_title),
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }
            else if (TextUtils.isEmpty(binding.EnterDate.text)){
                val snackbar = Snackbar.make(
                    binding.ParentLayout,
                    getString(R.string.add_date),
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }
            else if (TextUtils.isEmpty(binding.EnterTime.text)){
                val snackbar = Snackbar.make(
                    binding.ParentLayout,
                    getString(R.string.add_time),
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }
            else {

                val task = binding.userToDoEditText.text.toString()
                var tag = "tag"

                val formatter = SimpleDateFormat("d MMM yyyy", Locale.US)
                val formatter2 = SimpleDateFormat("d MM yyyy HH:mm", Locale.US)
                val mDate = formatter2.parse("$day $month $year $hr:$min")
                val tm = mDate.time
                val daymonthyear = formatter.format(mDate)

                replyIntent.putExtra(getString(R.string.task), tm)
                if (checked) {
                    val prefsEditor = appSharedPrefs.edit()
                    prefsEditor.putString(getString(R.string.task), binding.userToDoEditText.text.toString())
                    prefsEditor.apply()
                    val c = Calendar.getInstance()
                    c.set(yearR, monthR-1, dayR, hrR, minR)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    val notifyManager = OneTimeWorkRequest.Builder(Notify::class.java)
                        .setInitialDelay(
                            c.timeInMillis - System.currentTimeMillis(),
                            TimeUnit.MILLISECONDS
                        )
                        .addTag(c.timeInMillis.toString())
                        .build()
                    tag = c.timeInMillis.toString()
                    WorkManager.getInstance().enqueue(notifyManager)
                }
                replyIntent.putExtra("tag", tag)
                taskViewModel.insert(Task(task, tm, tag, daymonthyear, System.currentTimeMillis()))
                fragmentManager?.popBackStack()
            }
        }
        return binding.root
    }




}
