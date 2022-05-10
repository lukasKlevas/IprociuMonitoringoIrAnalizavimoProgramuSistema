package com.habit.tracker.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.User
import com.habit.tracker.Repositories.RegisterRepository

class RegisterViewModel(application: Application) : AndroidViewModel(application)
{
    private lateinit var registerRepository: RegisterRepository
    private lateinit var responseLiveData: MutableLiveData<DataBaseResponse>
    fun init()
    {
        registerRepository=RegisterRepository()
        responseLiveData=registerRepository.getRegisterResponse()
    }


    fun createUser(email: String,password: String) {
        registerRepository.createUser(email,password);
    }

    fun getRegisterResponse(): LiveData<DataBaseResponse>
    {
        return responseLiveData
    }
}