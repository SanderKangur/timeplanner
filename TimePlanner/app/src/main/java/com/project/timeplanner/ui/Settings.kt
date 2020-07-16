package com.project.timeplanner.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.project.timeplanner.MainActivity
import com.project.timeplanner.R
import com.project.timeplanner.datamodels.TaskRepository
import com.project.timeplanner.datamodels.TaskRoomDatabase
import com.project.timeplanner.datamodels.TaskViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope.coroutineContext

class Settings : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    var pref: SwitchPreference? = null
    var pref2: Preference? = null

    //val wordsDao = TaskRoomDatabase.getDatabase(activity!!.application, CoroutineScope(coroutineContext)).taskDao()
    //val repo = TaskRepository(wordsDao, "None")


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_todo)
        val pmanager = preferenceManager
        pref = findPreference("nightMode")
        pref2 = findPreference("clearall")

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onStop() {
        super.onStop()
        // unregister the preference change listener
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        val appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity!!.application)
        // register the preference change listener
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
        if (appSharedPrefs.getBoolean("clearall", false)){
            val editor1 = appSharedPrefs.edit()
            editor1.putBoolean("clearall", false)
            val taskViewModel = TaskViewModel(activity!!.application, "None")
            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.confirm))
            builder.setMessage(getString(R.string.areyousure))

            builder.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
                taskViewModel.deleteAll()
                editor1.commit()
                dialog.dismiss()
            })

            builder.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener { dialog, which ->
                editor1.commit()
                dialog.dismiss()

            })

            val alert = builder.create()
            alert.show()
        }
    }




}