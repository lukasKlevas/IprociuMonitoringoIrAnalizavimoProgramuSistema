package com.habit.tracker.Activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.habit.tracker.Utils.Util
import com.habit.tracker.ViewModels.RegisterViewModel
import com.habit.tracker.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity(), View.OnClickListener
{

    lateinit var binding : ActivityRegisterBinding
    lateinit var viewModel: RegisterViewModel
    lateinit var progressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog=Util.getProgressDialog(this)
        viewModel=ViewModelProvider(this).get(RegisterViewModel::class.java)
        viewModel.init()
        setClickListeners()
        viewModel.getRegisterResponse().observe(this, Observer {
            if(it!=null)
            {
                if(progressDialog.isShowing)
                    progressDialog.dismiss()
                if(it.isSuccess)
                {
                    Util.showMessage(this, "Account Created Successfully")
                    onBackPressed()
                }
                else
                {
                    Util.showMessageDialog(this,it.message.toString())
                }
            }
        })
    }

    private fun setClickListeners() {
        binding.txtSignIn.setOnClickListener(this)
        binding.btnCreateAccount.setOnClickListener(this)
    }

    override fun onClick(view: View?)
    {
        if(view==binding.txtSignIn)
            finish()
        if(view==binding.btnCreateAccount)
            createAccount()
    }

    private fun createAccount() {
        with(binding)
        {
            if(!Util.isEmpty(binding.inputLayoutEmail,binding.inputLayoutPassword,binding.inputLayoutConfirmPassword))
            {
                if(inputLayoutPassword.editText!!.text.toString().equals(binding.inputLayoutConfirmPassword.editText!!.text.toString())){
                    progressDialog.show()
                viewModel.createUser(binding.inputLayoutEmail.editText!!.text.toString(),binding.inputLayoutPassword.editText!!.text.toString())
                }
            }
        }
    }
}