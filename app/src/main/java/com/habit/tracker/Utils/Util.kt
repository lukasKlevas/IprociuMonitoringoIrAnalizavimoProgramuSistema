package com.habit.tracker.Utils

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.habit.tracker.Enums.Frequency
import com.habit.tracker.MyNotificationManager
import com.habit.tracker.NotificationBroadCast
import com.habit.tracker.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Util {
    companion object
    {
        fun showMessageDialog(context: Context,message:String ): Dialog
        {
            val dialog=Dialog(context)
            dialog.setContentView(R.layout.lyt_dialog_message)
            val window: Window=dialog.getWindow()!!
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val txtMessage=dialog.findViewById<TextView>(R.id.txtMessage)
            val btnOkay: Button=dialog.findViewById(R.id.btnOkay)
            txtMessage.text=message
            btnOkay.setOnClickListener{dialog.dismiss()}
            dialog.show();
            return dialog
        }

        fun getDialog(context: Context,layout:Int): Dialog
        {
            val dialog=Dialog(context)
            dialog.setContentView(layout)
            val window: Window=dialog.getWindow()!!
            window.setGravity(Gravity.CENTER)
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            return dialog
        }

        fun showMessageDialog2(context: Context,message:String ): Dialog
        {
            val dialog=Dialog(context)
            dialog.setContentView(R.layout.lyt_dialog_message_2)
            val window: Window=dialog.getWindow()!!
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)

            val txtMessage=dialog.findViewById<TextView>(R.id.txtMessage)
            txtMessage.text=message
            dialog.show();
            return dialog
        }


        fun getProgressDialog(context: Context ): Dialog
        {
            val dialog=Dialog(context)
            dialog.setContentView(R.layout.lyt_progress)
            val window: Window=dialog.getWindow()!!
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            return dialog
        }

        fun showMessage(context: Context?, message: String?)
        {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun isEmpty(vararg  textInputLayouts: TextInputLayout ):Boolean
        {
            textInputLayouts.forEach { textinputlayout ->
                if(textinputlayout.editText!!.text.isEmpty())
                {
                    textinputlayout.editText!!.error="Required Field"
                    textinputlayout.editText!!.requestFocus()
                    return true
                }
            }
            return false
        }

        fun getCurrentDate(): String {
            return SimpleDateFormat(Constants.DATE_PATTERN, Locale.ENGLISH).format(Date())
        }

        fun getDateOfDaysOfWeek(): List<String>
        {
            val simpleDateFormat=SimpleDateFormat(Constants.DATE_PATTERN, Locale.ENGLISH)
            val calendar: Calendar=Calendar.getInstance()
            calendar.firstDayOfWeek=Calendar.MONDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

            val days: MutableList<String> = ArrayList()
            for (i in 0..6)
            {
                days.add(simpleDateFormat.format(calendar.time))
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            return days
        }

        fun cancelReminder(context: Context,requestCode:Int)
        {
            val alarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            val intent=Intent(context, NotificationBroadCast::class.java)
            val pendingintent=
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // ?
                    PendingIntent.getBroadcast(context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getBroadcast(context, requestCode, intent, 0)
                }
            alarmManager!!.cancel(pendingintent)
        }
        fun reminderNotification(context: Context,message: String,requestCode:Int,frequency: Frequency)
        {
            val myNotificationManager=MyNotificationManager(context)
            val Time=System.currentTimeMillis()
            var timeInterval=0L;
            if(frequency==Frequency.DAILY)
                timeInterval=getDayInMillis(1)
            if(frequency==Frequency.WEEKLY)
                timeInterval=getDayInMillis(7)
            if(frequency==Frequency.MONTHLY)
                timeInterval=getDayInMillis(30)

            val Reminder=Time + timeInterval
            myNotificationManager.setReminder(Reminder, message, requestCode)
            showMessage(context,
                "Notification will be shown after  " + formate(timeInterval)
            )
        }
        fun getDayInMillis(days:Long):Long
        {
            return TimeUnit.DAYS.toMillis(days)
        }
        fun formate(millis: Long): String
        {
            val days=TimeUnit.MILLISECONDS.toDays(millis);
            return String.format(
                "%02d Days", days
            )
        }
    }
}