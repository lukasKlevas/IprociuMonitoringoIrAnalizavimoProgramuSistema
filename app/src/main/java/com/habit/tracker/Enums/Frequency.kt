package com.habit.tracker.Enums

enum class Frequency
{
    WEEKLY,DAILY,MONTHLY;

    companion object
    {
        fun getSelectedFrequency(frequency: Frequency):String
        {
            if(frequency==WEEKLY)
            {
                return "This Week"
            }
            if(frequency==DAILY)
            {
                return "Today"
            }
            if(frequency==MONTHLY)
            {
                return "This Month"
            }
            return ""
        }
    }
}