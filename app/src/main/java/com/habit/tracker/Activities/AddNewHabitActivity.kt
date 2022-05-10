package com.habit.tracker.Activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.habit.tracker.Enums.Frequency
import com.habit.tracker.Interface.Response
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.Habit
import com.habit.tracker.R
import com.habit.tracker.Utils.Constants
import com.habit.tracker.Utils.DatabaseOperations
import com.habit.tracker.Utils.Util
import com.habit.tracker.databinding.ActivityAddNewHabitBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.util.*


class AddNewHabitActivity : AppCompatActivity(), View.OnClickListener {


    lateinit var binding:ActivityAddNewHabitBinding
    var color:Int=0
    private lateinit var habit:Habit
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding=ActivityAddNewHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCreate.setOnClickListener(this)
        binding.colorChooser.setOnClickListener(this)

        if(intent.extras!=null && intent.extras!!.containsKey("Habit"))
        {
            habit=intent.extras!!.getSerializable("Habit") as Habit
            setVals()
        }
    }
    private fun setVals()
    {
        with(binding)
        {
            color=habit.color
            editTextHabitName.setText(habit.name)
            spinnerNotification.setSelection(getFrequencyIndex(habit.notification))
            spinnerGoalFrequency.setSelection(getFrequencyIndex(habit.frequency))
            spinnerGoal.setSelection(getGoalIndex(habit.goal))
            checkboxReminder.isChecked=habit.reminder
            if(habit.color!=0)
            {
                colorChooser.setBackgroundColor(habit.color)
            }
            btnCreate.text=getString(R.string.update)
        }
    }

    override fun onClick(view: View?)
    {
        if(view==binding.btnCreate)
        {
            if(isValid())
            {
                val progressDialog=Util.getProgressDialog(this)
                progressDialog.show()
                val newHabit=getHabit()
                if(::habit.isInitialized)
                {
                    newHabit.id=habit.id
                    newHabit.dateCreated=habit.id
                    newHabit.timestamp=habit.timestamp
                    newHabit.requestCode=habit.requestCode

                    if(newHabit.requestCode==0)
                        newHabit.requestCode=Random().nextInt(1000)

                    if (binding.checkboxReminder.isChecked)
                    {
                        setNotification(newHabit)
                    }
                    else
                    {
                        Util.cancelReminder(this,newHabit.requestCode);
                    }
                    DatabaseOperations.updateHabit(newHabit,object : Response
                    {
                        override fun onDataBaseResponse(response: DataBaseResponse)
                        {
                            if(response.isSuccess)
                            {
                                Util.showMessage(this@AddNewHabitActivity,"Habit Updated")
                                finish()
                            }
                            else
                            {
                                Util.showMessageDialog(this@AddNewHabitActivity,response.message)
                            }
                        }
                    })
                }
                else
                {
                    newHabit.requestCode=Random().nextInt(1000)
                    if (binding.checkboxReminder.isChecked) {
                        setNotification(newHabit)
                    }
                    DatabaseOperations.saveToDb(newHabit,Constants.REF_HABIT,newHabit.id,object : Response
                    {
                        override fun onDataBaseResponse(response: DataBaseResponse) {
                            if(response.isSuccess)
                            {
                                Util.showMessage(this@AddNewHabitActivity,"Habit Saved")
                                finish()
                            }
                            else
                            {
                                Util.showMessageDialog(this@AddNewHabitActivity,response.message)
                            }
                        }
                    })
                }
            }
        }
        if(view==binding.colorChooser)
        {
            chooseColor()
        }
    }

    private fun setNotification(habit:Habit) {
        Util.reminderNotification(this,"Its time for : "+habit.name,habit.requestCode,habit.notification)
    }

    private fun chooseColor()
    {
        ColorPickerDialog.Builder(this)
            .setTitle("Pick a colour")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Ok",object : ColorEnvelopeListener
            {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    color=envelope!!.color
                    binding.colorChooser.setBackgroundColor(envelope.color)
                }
            })
            .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun getHabit(): Habit {
        return Habit(DatabaseOperations.getRandDocIdForCollection(Constants.REF_HABIT),
            FirebaseAuth.getInstance().currentUser!!.uid,
            binding.editTextHabitName.text.toString(),
            color,
            getGoalFrequency(),
            getGoal(),
            getNotificationFrequency(),
            binding.checkboxReminder.isChecked,
            Util.getCurrentDate())
    }

    private fun getGoalFrequency(): Frequency {
        return when(binding.spinnerGoalFrequency.selectedItemPosition) {
            0-> Frequency.DAILY
            1-> Frequency.WEEKLY
            2-> Frequency.MONTHLY
            else -> Frequency.DAILY
        }
    }

    private fun getGoal():Int
    {
        return binding.spinnerGoal.selectedItem.toString().toInt()
    }

    private fun getNotificationFrequency(): Frequency {
        when(binding.spinnerNotification.selectedItemPosition)
        {
            0->
                return Frequency.DAILY
            1->
                return Frequency.WEEKLY
            2->
                return Frequency.MONTHLY
            else ->
                return Frequency.DAILY
        }
    }

    private fun getGoalIndex(goal:Int):Int
    {
        return goal
    }

    private fun getFrequencyIndex(frequency: Frequency): Int {
        return when(frequency) {
            Frequency.DAILY-> 0
            Frequency.WEEKLY-> 1
            Frequency.MONTHLY-> 2
        }
    }

    private fun isValid(): Boolean
    {
        with(binding)
        {
            if (editTextHabitName.text.isEmpty())
            {
                editTextHabitName.error="Required Field"
                editTextHabitName.requestFocus()
                return false
            }
            return true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}