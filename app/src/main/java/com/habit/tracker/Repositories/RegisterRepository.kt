package com.habit.tracker.Repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.habit.tracker.Interface.Response
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.User
import com.habit.tracker.Utils.Constants
import com.habit.tracker.Utils.DatabaseOperations
import com.habit.tracker.Utils.Util
import java.util.*

class RegisterRepository
{
    private var responseLiveData: MutableLiveData<DataBaseResponse> = MutableLiveData()

    fun getRegisterResponse(): MutableLiveData<DataBaseResponse>
    {
        return responseLiveData
    }

    fun createUser(email: String,password: String)
    {
        DatabaseOperations.createUser(email,password,object : Response
        {
            override fun onDataBaseResponse(response: DataBaseResponse) {
                val registerResponse=DataBaseResponse()
                if(response.isSuccess)
                {
                    val user=User(FirebaseAuth.getInstance().uid.toString(),email,Util.getCurrentDate())
                    DatabaseOperations.saveToDb(user,Constants.REF_USERS,user.userid,object :Response
                    {
                        override fun onDataBaseResponse(response: DataBaseResponse) {
                            if(response.isSuccess)
                            {
                                registerResponse.isSuccess=true
                                registerResponse.message="Success"
                                registerResponse.data=user
                                responseLiveData.value=registerResponse
                            }
                            else
                            {
                                registerResponse.isSuccess=false
                                registerResponse.message=response.message
                                responseLiveData.value=registerResponse
                            }
                        }
                    })
                }
                else
                {
                    registerResponse.isSuccess=false
                    registerResponse.message=response.message
                    responseLiveData.value=registerResponse
                }
            }
        })
    }
}