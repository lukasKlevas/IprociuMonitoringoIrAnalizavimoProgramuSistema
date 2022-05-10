package com.habit.tracker.Utils

import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.habit.tracker.Interface.OnBottomItemSelected
import com.habit.tracker.MainActivity
import com.habit.tracker.R

class BottomNavigation(_activity: MainActivity, _onBottomItemSelected: OnBottomItemSelected) : View.OnClickListener {
    private val activity:MainActivity=_activity
    private val parentView:LinearLayout
    var selected: MaterialCardView?=null
    val onBottomItemSelected:OnBottomItemSelected=_onBottomItemSelected

    init
    {
        val lyt=activity.findViewById<LinearLayout>(R.id.lytNav)
        parentView=lyt
        for(i in  0 until parentView.childCount)
        {
            val view=parentView.getChildAt(i)
            view.tag=i
            view.setOnClickListener(this)
        }
    }

    fun setSelected(pos:Int)
    {
        onClick(parentView.getChildAt(pos))
    }

    fun setFragment(fragment: Fragment)
    {
        activity.supportFragmentManager.beginTransaction().replace(R.id.frameLayout,fragment).commit()
    }

    override fun onClick(view: View?) {
        //if selected item is same as previous
        if(selected!=null && selected!!.tag as Int==view!!.tag as Int)
            return
        //unselecting previous item
        if (selected != null) unSelect(selected!!)
        //selecting current item
        select(view as MaterialCardView)
        onBottomItemSelected.onItemSelected(view.tag as Int)
    }

    fun select(cardView: MaterialCardView) {
        cardView.setCardBackgroundColor(activity.resources.getColor(R.color.white))
        cardView.elevation=10f
        selected=cardView
    }

    fun unSelect(cardView: MaterialCardView) {
        cardView.setCardBackgroundColor(activity.resources.getColor(R.color.gray))
        cardView.elevation=0f
    }
}