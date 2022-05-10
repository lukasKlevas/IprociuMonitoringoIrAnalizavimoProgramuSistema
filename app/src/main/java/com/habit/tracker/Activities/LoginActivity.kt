package com.habit.tracker.Activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.habit.tracker.MainActivity
import com.habit.tracker.R
import com.habit.tracker.Utils.Util
import com.habit.tracker.ViewModels.LoginViewModel
import com.habit.tracker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener
{

    lateinit var binding : ActivityLoginBinding
    lateinit var viewModel: LoginViewModel
    lateinit var progressDialog:Dialog
    private var mGoogleSignInClient: GoogleSignInClient?=null
    private val RC_SIGN_IN=100
    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser!=null)
        {
            finish()
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog=Util.getProgressDialog(this)
        viewModel=ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.init()
        setClickListeners()

        viewModel.getLoginResponse().observe(this, Observer {
            if(it!=null)
            {
                if(progressDialog.isShowing)
                    progressDialog.dismiss()
                if(it.isSuccess)
                {
                    finish()
                    startActivity(Intent(this,MainActivity::class.java))
                }
                else
                {
                    Util.showMessageDialog(this, it.message)
                }
            }
        })
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient=GoogleSignIn.getClient(this, gso)
    }

    private fun setClickListeners() {
        binding.txtCreateAccount.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)
        binding.txtForgotPass.setOnClickListener(this)
        binding.btnGmail.setOnClickListener(this)
    }
    override fun onClick(view: View?)
    {
        if(view==binding.btnGmail)
            loginWithGmail()
        if(view==binding.txtCreateAccount)
            startActivity(Intent(this,RegisterActivity::class.java))
        if(view==binding.btnSignIn)
            login()
        if(view==binding.txtForgotPass)
            forgetPass()
    }

    private fun forgetPass() {
        if(binding.editTextEmail.text!!.isNotEmpty())
        {
            val progressDialog=Util.getProgressDialog(this)
            progressDialog.show()
            FirebaseAuth.getInstance().sendPasswordResetEmail(binding.editTextEmail.text.toString())
                .addOnCompleteListener {
                    if(progressDialog.isShowing)
                        progressDialog.dismiss()
                    if(it.isSuccessful)
                        Util.showMessage(this,"Password reset link sent to : "+binding.editTextEmail.text.toString())
                    else
                        Util.showMessage(this,it.exception!!.message)
                }
        }else{
            binding.editTextEmail.error="Required Field"
            binding.editTextEmail.requestFocus()
        }
    }

    private fun login() {
        if(!Util.isEmpty(binding.inputLayoutEmail,binding.inputLayoutPassword))
        {
            progressDialog.show()
            viewModel.authenticateUser(binding.inputLayoutEmail.editText!!.text.toString(),binding.inputLayoutPassword.editText!!.text.toString())
        }
    }

    private fun loginWithGmail()
    {
        val signInIntent=mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val mAuth: FirebaseAuth=FirebaseAuth.getInstance()
        if (requestCode == RC_SIGN_IN) {
            val taskSignIn: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account=taskSignIn.getResult(ApiException::class.java)
                if (account != null) {
                    val credential=GoogleAuthProvider.getCredential(account.idToken, null)
                    mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this
                        ) { task: Task<AuthResult?> ->
                            if (task.isSuccessful)
                            {
                                finish()
                                Toast.makeText(this@LoginActivity,
                                   "Success",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this@LoginActivity,
                                    MainActivity::class.java
                                )
                                )
                            } else {
                                showErrorMessage(task.exception!!.message)
                            }
                        }
                } else {
                    showErrorMessage("")
                }
            } catch (e: ApiException) {
                showErrorMessage(e.message)
            }
        }
    }

    private fun showErrorMessage(message: String?) {
        Toast.makeText(this, "Error : $message", Toast.LENGTH_LONG).show()
    }
}