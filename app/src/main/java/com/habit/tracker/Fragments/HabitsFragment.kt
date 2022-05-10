package com.habit.tracker.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.Query
import com.habit.tracker.Activities.AddNewHabitActivity
import com.habit.tracker.Activities.HabitReportActivity
import com.habit.tracker.Adapter.HabitAdapter
import com.habit.tracker.Interface.OnItemClick
import com.habit.tracker.Interface.Response
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.Habit
import com.habit.tracker.R
import com.habit.tracker.Utils.DatabaseOperations
import com.habit.tracker.Utils.Util
import com.habit.tracker.databinding.FragmentHabitsBinding
import java.util.Comparator


class HabitsFragment : Fragment(), View.OnClickListener,OnItemClick {



    lateinit var binding:FragmentHabitsBinding
    lateinit var habitAdapter: HabitAdapter
    var habitsList:MutableList<Habit> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        binding=FragmentHabitsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabadd.setOnClickListener(this)
        habitAdapter=HabitAdapter(requireContext(),this)
        binding.recylerView.adapter=habitAdapter
        loadData()
    }

    private fun loadData() {
        showView(binding.progressBar)
        DatabaseOperations.readAllHabits(object:Response
        {
            override fun onDataBaseResponse(response: DataBaseResponse)
            {
                if(response.isSuccess && (response.data!! as List<*>).isNotEmpty())
                {
                    showView(binding.lytData)
                    habitsList=response.data!! as MutableList<Habit>
                    habitAdapter.setData(habitsList)
                    loadCount()

                    binding.spinnerSortBy.onItemSelectedListener=object : AdapterView.OnItemSelectedListener
                    {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                            habitsList.sortWith(object :Comparator<Habit>
                            {
                                override fun compare(habit1: Habit, habit2: Habit): Int
                                {
                                    if(position==0)
                                    {
                                        if (habit1.timestamp < habit2.timestamp) return 1;
                                        if (habit1.timestamp > habit2.timestamp) return -1;
                                        else return 0;
                                    }
                                    else
                                    if(position==1)
                                    {
                                      return habit1.name.compareTo(habit2.name)
                                    }
                                    else
                                    if(position==2)
                                    {
                                        if (habit1.count < habit2.count) return 1;
                                        if (habit1.count > habit2.count) return -1;
                                        else return 0;
                                    }
                                 return 0
                                }
                            })
                            habitAdapter.notifyDataSetChanged()
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?){}
                    }
                }
                else
                {
                    showView(binding.txtMessage)
                }
            }
        },"timestamp", Query.Direction.DESCENDING)
    }

    private fun loadCount()
    {
        for(index in habitsList.indices)
        {
            val habit=habitsList[index]
            DatabaseOperations.readHabitCount(habit,object : Response
            {
                override fun onDataBaseResponse(response: DataBaseResponse) {
                    if(response.isSuccess)
                    {
                        habit.count=response.data as Int
                    }
                    else
                    {
                        habit.count=-2;
                    }
                    habitAdapter.notifyItemChanged(index)
                }
            })
        }
    }

    fun showView(view: View)
    {
        binding.progressBar.visibility=View.GONE
        binding.lytData.visibility=View.GONE
        binding.txtMessage.visibility=View.GONE
        view.visibility=View.VISIBLE
    }

    override fun onClick(view: View?) {
        if(view==binding.fabadd)
        {
            startActivity(Intent(requireContext(),AddNewHabitActivity::class.java))
        }
    }

    fun refresh() {
        loadData()
    }

    override fun onClick(pos: Int) {
      startActivity(Intent(context, HabitReportActivity::class.java).putExtra("Habit",habitsList[pos]))
    }

    override fun onEditClick(pos: Int) {
      startActivity(Intent(context,AddNewHabitActivity::class.java).putExtra("Habit",habitsList[pos]))
    }

    override fun onDeleteClick(pos: Int) {
        val dialog=Util.showMessageDialog2(requireContext(),"Are you sure to delete this ? ");
        val btnNo: MaterialButton=dialog.findViewById(R.id.btnNo)
        val btnYes: MaterialButton=dialog.findViewById(R.id.btnYes)
        dialog.show()

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        btnYes.setOnClickListener {
            dialog.dismiss()
            removeHabit(pos)
        }
    }

    private fun removeHabit(pos: Int)
    {
        val habit=habitsList[pos]
        val progressDialog=Util.getProgressDialog(requireContext())
        progressDialog.show()
        DatabaseOperations.removeHabit(habit, object :Response
        {
            override fun onDataBaseResponse(response: DataBaseResponse) {
                progressDialog.dismiss()
                if(response.isSuccess)
                {
                    Toast.makeText(requireContext(),"Habit Removed",Toast.LENGTH_LONG).show()
                    habitsList.removeAt(pos)
                    habitAdapter.notifyItemRemoved(pos)
                }
                else
                {
                    Util.showMessageDialog(requireContext(),"Error : "+response.message)
                }
            }
        })
    }
}