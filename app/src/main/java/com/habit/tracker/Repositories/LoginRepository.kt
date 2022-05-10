package com.habit.tracker.Repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.habit.tracker.Interface.Response
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.User
import com.habit.tracker.Utils.DatabaseOperations
import org.json.JSONObject


class LoginRepository
{
    private var loginResponseLiveData: MutableLiveData<DataBaseResponse> = MutableLiveData()
    fun getLoginResponse(): MutableLiveData<DataBaseResponse>
    {
        return loginResponseLiveData
    }

    fun authenticateUser(email: String, password: String)
    {
        DatabaseOperations.authenticateUser(email,password,object :Response
        {
            override fun onDataBaseResponse(response: DataBaseResponse) {
                loginResponseLiveData.value=response
            }
        })
    }
}