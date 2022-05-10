package com.habit.tracker.Fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.Query
import com.habit.tracker.Enums.Frequency
import com.habit.tracker.Interface.Response
import com.habit.tracker.Model.ChartData
import com.habit.tracker.Model.DataBaseResponse
import com.habit.tracker.Model.Habit
import com.habit.tracker.Utils.Constants
import com.habit.tracker.Utils.DatabaseOperations
import com.habit.tracker.Utils.Util
import com.habit.tracker.databinding.FragmentChartBinding
import java.text.SimpleDateFormat
import java.util.*

class ChartFragment : Fragment(), View.OnClickListener {
    lateinit var binding:FragmentChartBinding
    var habitsList:MutableList<Habit> = ArrayList()
    var chartList:MutableList<ChartData> = ArrayList()
    lateinit var lineChart: LineChart
    var startDate :Long=0L
    var endDate:Long = 0L
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding=FragmentChartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineChart=binding.reportingChart
        binding.btnStartDate.setOnClickListener(this)
        binding.btnEndDate.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }
    private fun configureLineChart() {
        val desc=Description()
        desc.text=""
        desc.textSize=28f
        lineChart.description=desc
        val xAxis=lineChart.xAxis
        lineChart.getAxis(YAxis.AxisDependency.LEFT).axisMinimum=0f

        lineChart.getAxis(YAxis.AxisDependency.LEFT).granularity = 1.0f;
        lineChart.getAxis(YAxis.AxisDependency.LEFT).isGranularityEnabled = true
        lineChart.getAxis(YAxis.AxisDependency.RIGHT).granularity = 1.0f;
        lineChart.getAxis(YAxis.AxisDependency.RIGHT).isGranularityEnabled = true
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.axisRight.axisMinimum = 0f

        xAxis.valueFormatter=object : ValueFormatter()
        {
            private val mFormat=SimpleDateFormat("dd MMM", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(value.toLong()))
            }
        }
    }

    private fun loadData() {
        lineChart.clear()
        showView(binding.progressBar)
        DatabaseOperations.readAllHabits(object: Response
        {
            override fun onDataBaseResponse(response: DataBaseResponse)
            {
                if(response.isSuccess && (response.data!! as List<*>).isNotEmpty())
                {
                    showView(binding.reportingChart)
                    habitsList=response.data!! as MutableList<Habit>
                    loadCount()
                }
                else
                {
                    showView(binding.txtMessage)
                }
            }
        },"timestamp", Query.Direction.ASCENDING)
    }

    private fun loadCount()
    {
        var count=0
        chartList.clear()
        for(index in habitsList.indices)
        {
            val habit=habitsList[index]
            habit.frequency=Frequency.WEEKLY
            DatabaseOperations.readHabitCountList(habit,object : Response
            {
                override fun onDataBaseResponse(response: DataBaseResponse) {
                    if(response.isSuccess)
                    {
                        chartList.add(ChartData(habit.name, response.data as List<Entry>,habit.color))
                    }
                    count++
                    if(count==habitsList.size)
                    {
                        configureLineChart()
                        setLineChartData(chartList)
                    }
                }
            },startDate,endDate)
        }
    }

    fun showView(view: View)
    {
        binding.progressBar.visibility=View.GONE
        binding.reportingChart.visibility=View.GONE
        binding.txtMessage.visibility=View.GONE
        view.visibility=View.VISIBLE
    }

    private fun setLineChartData(chartData: List<ChartData>) 
    {
        val dataSets: ArrayList<ILineDataSet> = ArrayList<ILineDataSet>()
        val comparator: Comparator<Entry> =
            Comparator<Entry> { o1, o2 -> java.lang.Float.compare(o1.x, o2.x) }
        for (chartData1 in chartData)
        {
            chartData1.entries.sortedWith(comparator)
            val closeLineDataSet=LineDataSet(chartData1.entries, chartData1.label)
            closeLineDataSet.setDrawCircles(true)
            closeLineDataSet.circleRadius=6f //4f
            closeLineDataSet.setDrawValues(true)
            closeLineDataSet.lineWidth=4f //3f
            closeLineDataSet.valueTextSize=16f
            if(chartData1.color==0)
            {
                closeLineDataSet.color=Color.BLACK
                closeLineDataSet.setCircleColor(Color.BLACK)
            }
            else
            {
                closeLineDataSet.color=chartData1.color
                closeLineDataSet.setCircleColor(chartData1.color)
            }
            dataSets.add(closeLineDataSet)
        }
        lineChart.data=LineData(dataSets);
        lineChart.invalidate()
    }

    private fun showDatePickerDialog(btn: MaterialButton) {
        val datePickerDialog=DatePickerDialog(requireContext(),
            OnDateSetListener { datePicker, i, i1, i2 ->
                val calendar=Calendar.getInstance()
                calendar[Calendar.YEAR]=datePicker.year
                calendar[Calendar.MONTH]=datePicker.month
                calendar[Calendar.DAY_OF_MONTH]=datePicker.dayOfMonth
                if(btn.tag.toString() == "StartDate")
                {
                    startDate=calendar.timeInMillis
                    binding.btnEndDate.isEnabled=true
                    endDate=0
                    binding.btnEndDate.text=null
                }
                else
                {
                    endDate=calendar.timeInMillis
                }
                btn.text=SimpleDateFormat(Constants.DATE_PATTERN, Locale.ENGLISH).format(calendar.time)
            }, Calendar.getInstance().get(Calendar.YEAR),  Calendar.getInstance().get(Calendar.MONTH),  Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate=Calendar.getInstance().timeInMillis
        if(btn.tag.toString() == "EndDate")
        {
            datePickerDialog.datePicker.minDate=startDate
        }
        datePickerDialog.show()
    }

    override fun onClick(view: View)
    {
        if(view==binding.btnStartDate || view==binding.btnEndDate)
            showDatePickerDialog(view as MaterialButton)
        if(view==binding.btnSubmit)
            if(binding.btnStartDate.text.isEmpty() || binding.btnEndDate.text.isEmpty())
                Util.showMessageDialog(requireContext(),"Please Select Start And End Date")
            else
                loadData()
    }
}