package com.habit.tracker.Model

import com.habit.tracker.Utils.Util
import java.util.*

data class HabitCount(val count: Int,
                      var id: String,
                      val uid: String,
                      val habit_id: String,
                      val date: String=Util.getCurrentDate(),
                      val week: Int=Calendar.getInstance().get(Calendar.WEEK_OF_MONTH),
                      val month: Int=Calendar.getInstance().get(Calendar.MONTH),
                      val year: Int=Calendar.getInstance().get(Calendar.YEAR),
                      val day: Int=Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                      val timestamp: Long=Calendar.getInstance().timeInMillis)
{
   constructor():this(0,"","","")
}