package com.habit.tracker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.habit.tracker.Fragments.ChartFragment
import com.habit.tracker.Fragments.HabitsFragment
import com.habit.tracker.Fragments.ProfileFragment
import com.habit.tracker.Interface.OnBottomItemSelected
import com.habit.tracker.Utils.BottomNavigation
import com.habit.tracker.Utils.Util
import com.habit.tracker.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener, OnBottomItemSelected {
    lateinit var binding: ActivityMainBinding
    lateinit var bottomNav:BottomNavigation
    lateinit var habitsFragment:HabitsFragment
    lateinit var profileFragment: ProfileFragment
    lateinit var chartFragment: ChartFragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        habitsFragment=HabitsFragment()
        profileFragment=ProfileFragment()
        chartFragment=ChartFragment()

        bottomNav=BottomNavigation(this,this)
        bottomNav.setSelected(0);
    }

    override fun onResume() {
        super.onResume()
        habitsFragment.refresh()
    }

    override fun onClick(view: View?){}

    override fun onItemSelected(position: Int)
    {
        if(position==0)
            bottomNav.setFragment(habitsFragment)
        if(position==1)
            bottomNav.setFragment(chartFragment)
        if(position==2)
            bottomNav.setFragment(profileFragment)
    }
}