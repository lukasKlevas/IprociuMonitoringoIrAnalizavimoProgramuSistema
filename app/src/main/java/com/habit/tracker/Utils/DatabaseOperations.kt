package com.habit.tracker.Utils

import com.github.mikephil.charting.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.habit.tracker.Enums.Frequency
import com.habit.tracker.Interface.Response
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.Habit
import com.habit.tracker.Model.HabitCount
import com.habit.tracker.Model.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DatabaseOperations
{
    companion object {
        fun createUser(email: String, password: String, response: Response) {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    val registerResponse=DataBaseResponse()
                    if (it.isSuccessful) {
                        val user=User(FirebaseAuth.getInstance().uid.toString(), email,
                            Util.getCurrentDate()
                        )
                        registerResponse.isSuccess=true
                        registerResponse.message="Success"
                        registerResponse.data=user
                    } else {
                        registerResponse.isSuccess=false
                        registerResponse.message=it.exception!!.message.toString()
                        registerResponse.data=null
                    }
                    response.onDataBaseResponse(registerResponse)
                }
        }

        fun getRandDocIdForCollection(collectionRef: String): String {
            return Firebase.firestore.collection(collectionRef).document().id
        }

        fun authenticateUser(email: String, password: String, response: Response) {
            val dataBaseResponse=DataBaseResponse()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    dataBaseResponse.message=
                        if (it.isSuccessful) "Success" else it.exception!!.message.toString()
                    dataBaseResponse.isSuccess=it.isSuccessful
                    response.onDataBaseResponse(dataBaseResponse)
                }
        }

        fun saveToDb(data: Any, collection: String, document: String, response: Response) {
            val collectionsRef=Firebase.firestore.collection(collection)
            collectionsRef.document(document)
                .set(data).addOnCompleteListener {
                    val registerResponse=DataBaseResponse()
                    if (it.isSuccessful) {
                        registerResponse.isSuccess=true
                        registerResponse.message="Success"
                        registerResponse.data=null
                    } else {
                        registerResponse.isSuccess=false
                        registerResponse.message=it.exception!!.message.toString()
                    }
                    response.onDataBaseResponse(registerResponse)
                }
        }

        fun modifyHabitCount(habitCount: HabitCount, dbresponse: Response) { //?
            val collectionsRef=Firebase.firestore.collection(Constants.REF_HABIT_COUNT)
            collectionsRef.document(habitCount.id).get().addOnCompleteListener {
                val dataBaseResponse=DataBaseResponse()
                if (it.isSuccessful) {
                    if (it.result.exists()) {
                        val hashMap: HashMap<String, Any> = HashMap()
                        if (it.result.toObject(HabitCount::class.java)!!.count == 0 && habitCount.count == -1) {
                            dataBaseResponse.isSuccess=false
                            dataBaseResponse.message="Frequency Cannot be less then 0"
                            dataBaseResponse.data=null
                            dbresponse.onDataBaseResponse(dataBaseResponse)
                        } else {
                            hashMap["count"]=FieldValue.increment(habitCount.count.toLong())
                            collectionsRef.document(habitCount.id).update(hashMap)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        dataBaseResponse.isSuccess=true
                                        dataBaseResponse.message="Success"
                                        dataBaseResponse.data=null
                                    } else {
                                        dataBaseResponse.isSuccess=false
                                        dataBaseResponse.message=it.exception!!.message.toString()
                                    }
                                    dbresponse.onDataBaseResponse(dataBaseResponse)
                                }
                        }
                    } else { // if new habit and never had any counts added
                        saveToDb(habitCount,
                            Constants.REF_HABIT_COUNT,
                            habitCount.id,
                            object : Response {
                                override fun onDataBaseResponse(response: DataBaseResponse) {
                                    dbresponse.onDataBaseResponse(response)
                                }
                            })
                    }
                } else {
                    dataBaseResponse.isSuccess=false
                    dataBaseResponse.message=it.exception!!.message.toString()
                    dbresponse.onDataBaseResponse(dataBaseResponse)
                }
            }
        }

        fun readAllHabits(response: Response,orderBy:String, direction: Query.Direction) {
            val collectionsRef=Firebase.firestore.collection(Constants.REF_HABIT)
            var query=
            collectionsRef.whereEqualTo("uid", FirebaseAuth.getInstance().currentUser!!.uid)
            query=query.orderBy(orderBy,direction)
            query.get().addOnCompleteListener {
                val registerResponse=DataBaseResponse()
                val habitsList: MutableList<Habit> = ArrayList()
                if (it.isSuccessful) {
                    for (document in it.result.documents)
                    {
                        val habit=document.toObject(Habit::class.java)
                        habitsList.add(habit!!)
                    }
                    registerResponse.isSuccess=true
                    registerResponse.message="Success"
                    registerResponse.data=habitsList
                } else {
                    registerResponse.isSuccess=false
                    registerResponse.message=it.exception!!.message.toString()
                }
                response.onDataBaseResponse(registerResponse)
            }
        }

        fun removeHabit(habit: Habit,response: Response) {
            val collectionsRef=Firebase.firestore.collection(Constants.REF_HABIT)
            collectionsRef.document(habit.id).delete().addOnCompleteListener {
                val registerResponse=DataBaseResponse()
                if (it.isSuccessful) {
                    registerResponse.isSuccess=true
                    registerResponse.message="Success"
                } else {
                    registerResponse.isSuccess=false
                    registerResponse.message=it.exception!!.message.toString()
                }
                response.onDataBaseResponse(registerResponse)
            }
        }

        fun updateHabit(habit: Habit,response: Response) {
            val collectionsRef=Firebase.firestore.collection(Constants.REF_HABIT)
            collectionsRef.document(habit.id).set(habit).addOnCompleteListener {
                val registerResponse=DataBaseResponse()
                if (it.isSuccessful) {
                    registerResponse.isSuccess=true
                    registerResponse.message="Success"
                } else {
                    registerResponse.isSuccess=false
                    registerResponse.message=it.exception!!.message.toString()
                }
                response.onDataBaseResponse(registerResponse)
            }
        }

        fun readHabitCount(habitID: String, response: Response) {
            val collectionsRef=Firebase.firestore.collection(Constants.REF_HABIT_COUNT)
            collectionsRef.document(habitID).get().addOnCompleteListener {
                val registerResponse=DataBaseResponse()
                if (it.isSuccessful) {
                    var habitCount: HabitCount?=null
                    if (it.result.exists()) {
                        habitCount=it.result.toObject(HabitCount::class.java)
                    }
                    registerResponse.isSuccess=true
                    registerResponse.message="Success"
                    registerResponse.data=habitCount
                } else {
                    registerResponse.isSuccess=false
                    registerResponse.message=it.exception!!.message.toString()
                }
                response.onDataBaseResponse(registerResponse)
            }
        }

        fun readHabitCount(habit: Habit, response: Response) {
            val collectionsRef=Firebase.firestore.collection(Constants.REF_HABIT_COUNT)
            var query=collectionsRef
                .whereEqualTo("uid", habit.uid)
                .whereEqualTo("habit_id", habit.id)
                .whereEqualTo("year", Calendar.getInstance().get(Calendar.YEAR))
                .whereEqualTo("month", Calendar.getInstance().get(Calendar.MONTH))
            if (habit.frequency == Frequency.DAILY) {
                query=query.whereEqualTo("week", Calendar.getInstance().get(Calendar.WEEK_OF_MONTH))
                    .whereEqualTo("day", Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
            }
            if (habit.frequency == Frequency.WEEKLY) {
                query=query.whereEqualTo("week", Calendar.getInstance().get(Calendar.WEEK_OF_MONTH))
            }
            query.get().addOnCompleteListener {
                val registerResponse=DataBaseResponse()
                if (it.isSuccessful) {
                    var habitCount: Int=0
                    if (!it.result.isEmpty) {
                        for (document in it.result) {
                            habitCount+=document.toObject(HabitCount::class.java).count
                        }
                    }
                    registerResponse.isSuccess=true
                    registerResponse.message="Success"
                    registerResponse.data=habitCount
                } else {
                    registerResponse.isSuccess=false
                    registerResponse.message=it.exception!!.message.toString()
                }
                response.onDataBaseResponse(registerResponse)
            }
        }

        fun readHabitCountList(habit: Habit, response: Response,startDate: Long,
                               endDate: Long) {
            val collectionsRef=Firebase.firestore.collection(Constants.REF_HABIT_COUNT)
            var query=collectionsRef
                .whereEqualTo("uid", habit.uid)
                .whereEqualTo("habit_id", habit.id)
                .whereGreaterThanOrEqualTo("timestamp",startDate)
                .whereLessThanOrEqualTo("timestamp",endDate);
            val hashMapIndex:HashMap<String,Int> = HashMap()
            val entriesList:MutableList<Entry> = getZerosList(hashMapIndex,startDate,endDate)

                query.get().addOnCompleteListener {
                val registerResponse=DataBaseResponse()
                if (it.isSuccessful) {
                    if (!it.result.isEmpty) {
                        for (document in it.result)
                        {
                            val habitcount=document.toObject(HabitCount::class.java)
                            val calendar=Calendar.getInstance();
                            calendar.set(Calendar.YEAR,habitcount.year)
                            calendar.set(Calendar.MONTH,habitcount.month)
                            calendar.set(Calendar.DAY_OF_WEEK,habitcount.day)
                            calendar.set(Calendar.WEEK_OF_MONTH,habitcount.week)
                            val date=calendar.timeInMillis
                            if(hashMapIndex.containsKey(habitcount.date))
                            {
                                entriesList[hashMapIndex[habitcount.date]!!]=Entry(date.toFloat(), habitcount.count.toFloat())
                            }
                            else
                                entriesList.add(Entry(date.toFloat(), habitcount.count.toFloat()))
                        }
                    }
                    registerResponse.isSuccess=true
                    registerResponse.message="Success"
                    registerResponse.data=entriesList
                } else {
                    registerResponse.isSuccess=false
                    registerResponse.message=it.exception!!.message.toString()
                }
                response.onDataBaseResponse(registerResponse)
            }
        }

        private fun getZerosList(HashMap: HashMap<String, Int>, startDate: Long, endDate: Long): MutableList<Entry>
        {
            val list:MutableList<Entry> = ArrayList()
            val calendar: Calendar=Calendar.getInstance()
            calendar.timeInMillis=startDate
            var index=0
            while (true)
            {
                list.add(Entry(calendar.timeInMillis.toFloat(),0f))
                HashMap[SimpleDateFormat(Constants.DATE_PATTERN, Locale.ENGLISH).format(calendar.time)]=index
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                index++
                if(calendar.timeInMillis>endDate)
                {
                    break
                }
            }
            return list
        }
    }
}