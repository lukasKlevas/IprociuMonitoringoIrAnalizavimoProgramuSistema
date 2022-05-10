package com.habit.tracker.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.habit.tracker.Interface.Response
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.Habit
import com.habit.tracker.Model.HabitCount
import com.habit.tracker.Utils.DatabaseOperations
import com.habit.tracker.Utils.Util
import com.habit.tracker.databinding.ActivityHabitReportBinding
import java.util.*


class HabitReportActivity : AppCompatActivity(), View.OnClickListener {


    lateinit var binding:ActivityHabitReportBinding
    lateinit var habit:Habit
    lateinit var progressBarList:List<ProgressBar>
    lateinit var txtCountList:List<TextView>
    var totalCount:Int=0
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding=ActivityHabitReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding)
        {
            txtCountList=arrayListOf(txtCountMonday,txtCountTuesday,txtCountWednesday,txtCountThursday,txtCountFriday,txtCountSaturday,txtCountSunday)
            progressBarList=arrayListOf(progressMonday,progressTuesday,progressWednesday,progressThursday,progressFriday,progressSaturday,progressSunday)
        }

        habit=intent.getSerializableExtra("Habit") as Habit
        binding.fabadd.setOnClickListener(this)
        binding.fabminus.setOnClickListener(this)
        supportActionBar!!.title=habit.name
        readHabitsData()
    }

    private fun readHabitsData() {
        showLoading()
        totalCount=0;
        val dateOfAllDays=Util.getDateOfDaysOfWeek()
        for(index in dateOfAllDays.indices)
        {
            val id=habit.id+dateOfAllDays[index]
            DatabaseOperations.readHabitCount(id,object : Response
            {
                override fun onDataBaseResponse(response: DataBaseResponse)
                {
                    val progressBar=progressBarList[index]
                    val txtCount=txtCountList[index]
                    if(response.isSuccess)
                    {
                        if(response.data!=null)
                        {
                            val habitCount=response.data as HabitCount;
                            progressBar.progress=habitCount.count.toInt()
                            txtCount.text=habitCount.count.toString()+" Times"
                            totalCount+=habitCount.count.toInt()
                            binding.txtTotalCount.text="$totalCount/${habit.goal}";
                        }
                        else
                        {
                            progressBar.progress=0
                            txtCount.text="0 times"
                        }
                    }
                    else
                    {
                        progressBar.progress=0
                        txtCount.text="Error"
                    }
                }
            })
        }
    }

    private fun showLoading() {
        with(binding)
        {
            txtTotalCount.text="0/${habit.goal}"

            for(txtView in txtCountList)
                txtView.text="Loading..."

            for(progress in progressBarList)
                progress.max=habit.goal
        }
    }

    override fun onClick(view: View?)
    {
        if(view==binding.fabadd)
            modifyCount(1)
        if(view==binding.fabminus)
            modifyCount(-1)
    }

    private fun modifyCount(count:Int) {
        val uid=FirebaseAuth.getInstance().currentUser!!.uid;
        val id=habit.id+Util.getCurrentDate()
        val habitCount=HabitCount(count=count,id=id,uid = uid,habit_id = habit.id)
        val progressDialog=Util.getProgressDialog(this)
        progressDialog.show()
        DatabaseOperations.modifyHabitCount(habitCount,object : Response
        {
            override fun onDataBaseResponse(response: DataBaseResponse) {
                if(progressDialog.isShowing)
                    progressDialog.dismiss()
                if(response.isSuccess)
                {
                    readHabitsData();
                }
                else
                {
                    Util.showMessageDialog(this@HabitReportActivity,"Error : "+response.message)
                }
            }
        })
    }
}