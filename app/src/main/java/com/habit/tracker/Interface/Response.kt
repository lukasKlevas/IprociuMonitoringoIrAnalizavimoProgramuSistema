package com.habit.tracker.Interface

import com.habit.tracker.Model.DataBaseResponse

interface Response
{
    fun onDataBaseResponse(response: DataBaseResponse)
}