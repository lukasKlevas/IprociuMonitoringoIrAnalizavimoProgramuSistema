package com.habit.tracker.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.User
import com.habit.tracker.Repositories.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) // ? why :Application
{
    private lateinit var loginRepository: LoginRepository
    private lateinit var loginResponseLiveData: MutableLiveData<DataBaseResponse>
    fun init()
    {
        loginRepository=LoginRepository()
        loginResponseLiveData=loginRepository.getLoginResponse()
    }


    fun authenticateUser(email: String, password: String) {
        loginRepository.authenticateUser(email,password);
    }

    fun getLoginResponse(): LiveData<DataBaseResponse>
    {
        return loginResponseLiveData
    }
}