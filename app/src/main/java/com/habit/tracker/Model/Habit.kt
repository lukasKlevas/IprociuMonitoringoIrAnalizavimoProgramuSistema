package com.habit.tracker.Model

import com.habit.tracker.Enums.Frequency
import java.io.Serializable
import java.util.*

data class Habit(var id:String,
                 val uid:String,
                 val name:String,
                 val color:Int,
                 var frequency:Frequency,
                 val goal:Int,
                 val notification:Frequency,
                 val reminder:Boolean,
                 var dateCreated:String,
                 var requestCode:Int=0,
                 var count:Int=-1,
                 var timestamp: Long=Calendar.getInstance().timeInMillis) : Serializable
{
    constructor (): this(
        "",
        "",
        "",
        0,
        Frequency.DAILY,
        0,
        Frequency.DAILY,
        false,
        "")
}
