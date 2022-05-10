package com.habit.tracker.Interface

interface  OnItemClick {
   fun onClick(pos:Int)
   fun onEditClick(pos: Int){}
   fun onDeleteClick(pos: Int){}
}