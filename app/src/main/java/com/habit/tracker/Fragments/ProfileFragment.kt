package com.habit.tracker.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.habit.tracker.Activities.LoginActivity
import com.habit.tracker.R
import com.habit.tracker.Utils.Constants
import com.habit.tracker.Utils.Util
import com.habit.tracker.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment(), View.OnClickListener {


    lateinit var binding:FragmentProfileBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.cardLogout.setOnClickListener(this)
        binding.cardChangePassword.setOnClickListener(this)
        binding.cardContactSupport.setOnClickListener(this)
        binding.cardDeleteAccount.setOnClickListener(this)
        binding.cardContactSupport.setOnClickListener(this)

        val email=FirebaseAuth.getInstance().currentUser!!.email
        val timeStamp:Long=FirebaseAuth.getInstance().currentUser!!.metadata!!.creationTimestamp
        val calendar=Calendar.getInstance()
        calendar.timeInMillis=timeStamp
        binding.txtJoinedAt.text="Joined At :"+SimpleDateFormat(Constants.DATE_PATTERN, Locale.ENGLISH).format(calendar.time)
        binding.txtEmail.text="Email :$email"
    }

    override fun onClick(view: View?) {

        if(view== binding.cardLogout)
        {
            logOutUser()
        }
        if(view== binding.cardDeleteAccount)
        {
            deleteAccount()
        }
        if(view== binding.cardChangePassword)
        {
            changePassword()
        }
        if(view==binding.cardContactSupport)
        {
            openEmail()
        }
    }

    fun openEmail() {
        val intent=Intent(Intent.ACTION_SENDTO)
        intent.data=Uri.parse("mailto:ht.lk.assistance@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, "")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Write your query here...")
        startActivity(intent)
    }

    private fun changePassword() {
        val dialog=Util.getDialog(requireContext(),R.layout.lyt_password)
        val editTextPassword=dialog.findViewById<TextInputEditText>(R.id.editTextPassword)
        val btnUpdatePass=dialog.findViewById<MaterialButton>(R.id.btnUpdatePass)

        btnUpdatePass.setOnClickListener {
            if(editTextPassword.text!!.isNotEmpty())
            {
                dialog.dismiss()
                val progressDialog=Util.getProgressDialog(requireContext())
                progressDialog.show()
                FirebaseAuth.getInstance().currentUser!!.updatePassword(editTextPassword.text.toString())
                    .addOnCompleteListener {

                        if(progressDialog.isShowing)
                            progressDialog.dismiss()
                        if(it.isSuccessful)
                        {
                            Util.showMessage(requireContext(),"Password Updated")
                            logOutUser()
                        }
                        else
                        {
                            Util.showMessage(requireContext(),"Failed To Update Password"+it.exception!!.message)
                        }
                    }
            }

        }
        dialog.show()
    }

    private fun deleteAccount() {
        val dialog=Util.showMessageDialog2(requireContext(),"Are you sure to delete account ?")
        val btnYes=dialog.findViewById<MaterialButton>(R.id.btnYes)
        val btnNo=dialog.findViewById<MaterialButton>(R.id.btnNo)
        btnYes.setOnClickListener {
            dialog.dismiss()
            val progressDialog=Util.getProgressDialog(requireContext())
            progressDialog.show()
            FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener {
                if(progressDialog.isShowing)
                    progressDialog.dismiss()
                if(it.isSuccessful)
                {
                    Util.showMessage(requireContext(),"Account Deleted")
                    logOutUser()
                }
                else
                {
                    Util.showMessage(requireContext(),"Failed To Delete Account")
                }
            }
        }
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
        ActivityCompat.finishAffinity(requireActivity())
        requireActivity().finish()
        startActivity(Intent(requireContext(),LoginActivity::class.java))
    }

}