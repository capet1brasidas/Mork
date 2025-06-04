package com.example.fit5046a2.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fit5046a2.data.entity.Task
import com.example.fit5046a2.data.repository.TaskRepository
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(application)
    
    // LiveData for storing processed bar chart data
    private val _barEntries = MutableLiveData<List<BarEntry>>()
    val barEntries: LiveData<List<BarEntry>> = _barEntries
    
    // LiveData for duration chart data
    private val _durationBarEntries = MutableLiveData<List<BarEntry>>()
    val durationBarEntries: LiveData<List<BarEntry>> = _durationBarEntries
    
    // LiveData for storing x-axis labels
    private val _xAxisLabels = MutableLiveData<List<String>>()
    val xAxisLabels: LiveData<List<String>> = _xAxisLabels
    
    // Current time period filter
    private val _currentPeriod = MutableLiveData("day")
    val currentPeriod: LiveData<String> = _currentPeriod
    
    // Current analysis type
    private val _currentAnalysisType = MutableLiveData("Task Completion")
    val currentAnalysisType: LiveData<String> = _currentAnalysisType
    
    // User email for filtering
    private var userEmail: String? = null
    
    // Set the current period filter
    fun setPeriod(period: String) {
        _currentPeriod.value = period
        clearChartData()
        loadTaskCompletionData()
    }
    
    // Set the current analysis type
    fun setAnalysisType(type: String) {
        _currentAnalysisType.value = type
        clearChartData()
        loadTaskCompletionData()
    }

    fun clearChartData(){
        _barEntries.postValue(emptyList())
        _xAxisLabels.postValue(emptyList())
    }
    
    // Set the user email for filtering
    fun setUserEmail(email: String) {
        userEmail = email
        loadTaskCompletionData()
    }
    
    // Load task completion data based on the current period and user
    fun loadTaskCompletionData() {
        val period = _currentPeriod.value ?: "day"
        val analysisType = _currentAnalysisType.value ?: "Task Completion"
        val email = userEmail
        
        android.util.Log.d("AnalysisViewModel", "Loading task data: period=$period, analysisType=$analysisType, email=$email")
        
        // Log repository instance
        android.util.Log.d("AnalysisViewModel", "Repository instance: $repository")
        
        viewModelScope.launch(Dispatchers.IO) {
            android.util.Log.d("AnalysisViewModel", "Launched coroutine to fetch task data")
            if (email != null) {
                // Load data for a specific user
                when (period) {
                    "day" -> repository.getTodayCompletedTasksByUser(email).collect { tasks ->
                        android.util.Log.d("AnalysisViewModel", "Received ${tasks.size} daily tasks for user $email")
                        if (tasks.isEmpty()) {
                            android.util.Log.d("AnalysisViewModel", "No tasks found for $email for daily analysis")
                        } else {
                            tasks.forEach { task ->
                                android.util.Log.d("AnalysisViewModel", "Task found: id=${task.taskId}, title=${task.title}, completedAt=${task.completedAt}, status=${task.status}")
                            }
                        }
                        
                        when (analysisType) {
                            "Task Completion" -> processDailyTasks(tasks)
                            "Task Duration" -> processDailyTasksDuration(tasks)
                        }
                    }
                    "month" -> repository.getThisMonthCompletedTasksByUser(email).collect { tasks ->
                        android.util.Log.d("AnalysisViewModel", "Received ${tasks.size} monthly tasks for user $email")
                        if (tasks.isEmpty()) {
                            android.util.Log.d("AnalysisViewModel", "No tasks found for $email for monthly analysis")
                        } else {
                            tasks.forEach { task ->
                                android.util.Log.d("AnalysisViewModel", "Monthly Task: id=${task.taskId}, title=${task.title}, completedAt=${task.completedAt}, status=${task.status}")
                            }
                        }
                        
                        when (analysisType) {
                            "Task Completion" -> processMonthlyTasks(tasks)
                            "Task Duration" -> processMonthlyTasksDuration(tasks)
                        }
                    }
                    "year" -> repository.getThisYearCompletedTasksByUser(email).collect { tasks ->
                        android.util.Log.d("AnalysisViewModel", "Received ${tasks.size} yearly tasks for user $email")
                        if (tasks.isEmpty()) {
                            android.util.Log.d("AnalysisViewModel", "No tasks found for $email for yearly analysis")
                        } else {
                            tasks.forEach { task ->
                                android.util.Log.d("AnalysisViewModel", "Yearly Task: id=${task.taskId}, title=${task.title}, completedAt=${task.completedAt}, status=${task.status}")
                            }
                        }
                        
                        when (analysisType) {
                            "Task Completion" -> processYearlyTasks(tasks)
                            "Task Duration" -> processYearlyTasksDuration(tasks)
                        }
                    }
                }
            } else {
                // Load data for all users
                when (period) {
                    "day" -> repository.getTodayCompletedTasks().collect { 
                        when (analysisType) {
                            "Task Completion" -> processDailyTasks(it)
                            "Task Duration" -> processDailyTasksDuration(it)
                        }
                    }
                    "month" -> repository.getThisMonthCompletedTasks().collect { 
                        when (analysisType) {
                            "Task Completion" -> processMonthlyTasks(it)
                            "Task Duration" -> processMonthlyTasksDuration(it)
                        }
                    }
                    "year" -> repository.getThisYearCompletedTasks().collect { 
                        when (analysisType) {
                            "Task Completion" -> processYearlyTasks(it)
                            "Task Duration" -> processYearlyTasksDuration(it)
                        }
                    }
                }
            }
        }
    }
    
    // Process daily tasks into bar entries
    private fun processDailyTasks(tasks: List<Task>) {
        val datePairs = getLastNDates(5) // You can change 10 to however many days you want to show
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        datePairs.forEachIndexed { index, (label, millis) ->
            val dayStart = Calendar.getInstance().apply {
                timeInMillis = millis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayEnd = dayStart + 24 * 60 * 60 * 1000 // end of the day

            val count = tasks.count { it.completedAt in dayStart until dayEnd }
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
            labels.add(label)
        }

        _barEntries.postValue(entries)
        _xAxisLabels.postValue(labels)
    }


    // Process monthly tasks into bar entries
    private fun processMonthlyTasks(tasks: List<Task>) {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault()) // Month abbreviation
        
        // Get the last 5 months (or the specified analysis period)
        val months = mutableListOf<Calendar>()
        for (i in 4 downTo 0) {
            val monthCalendar = Calendar.getInstance()
            monthCalendar.add(Calendar.MONTH, -i)
            months.add(monthCalendar)
        }
        
        // Count tasks completed in each month
        val monthCountMap = mutableMapOf<Int, Int>()
        val monthLabels = mutableListOf<String>()
        
        months.forEachIndexed { index, month ->
            monthLabels.add(monthFormat.format(month.time))
            monthCountMap[index] = 0
            
            // Count tasks completed in this month
            tasks.forEach { task ->
                task.completedAt?.let { completedAt ->
                    val taskMonth = Calendar.getInstance()
                    taskMonth.timeInMillis = completedAt
                    
                    if (isSameMonth(taskMonth, month)) {
                        monthCountMap[index] = (monthCountMap[index] ?: 0) + 1
                    }
                }
            }
        }
        
        // Create bar entries
        val entries = monthCountMap.map { (month, count) -> 
            BarEntry(month.toFloat(), count.toFloat())
        }.sortedBy { it.x }
        
        // Update LiveData
        _barEntries.postValue(entries)
        _xAxisLabels.postValue(monthLabels)
    }
    
    // Process yearly tasks into bar entries
    private fun processYearlyTasks(tasks: List<Task>) {
        val calendar = Calendar.getInstance()
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        
        // Get the last 5 years (or the specified analysis period)
        val years = mutableListOf<Calendar>()
        for (i in 4 downTo 0) {
            val yearCalendar = Calendar.getInstance()
            yearCalendar.add(Calendar.YEAR, -i)
            years.add(yearCalendar)
        }
        
        // Count tasks completed in each year
        val yearCountMap = mutableMapOf<Int, Int>()
        val yearLabels = mutableListOf<String>()
        
        years.forEachIndexed { index, year ->
            yearLabels.add(yearFormat.format(year.time))
            yearCountMap[index] = 0
            
            // Count tasks completed in this year
            tasks.forEach { task ->
                task.completedAt?.let { completedAt ->
                    val taskYear = Calendar.getInstance()
                    taskYear.timeInMillis = completedAt
                    
                    if (isSameYear(taskYear, year)) {
                        yearCountMap[index] = (yearCountMap[index] ?: 0) + 1
                    }
                }
            }
        }
        
        // Create bar entries
        val entries = yearCountMap.map { (year, count) -> 
            BarEntry(year.toFloat(), count.toFloat())
        }.sortedBy { it.x }
        
        // Update LiveData
        _barEntries.postValue(entries)
        _xAxisLabels.postValue(yearLabels)
    }
    
    // Process daily tasks durations into bar entries
    private fun processDailyTasksDuration(tasks: List<Task>) {
        val datePairs = getLastNDates(5)
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        datePairs.forEachIndexed { index, (label, millis) ->
            val dayStart = Calendar.getInstance().apply {
                timeInMillis = millis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayEnd = dayStart + 24 * 60 * 60 * 1000

            val durationMillis = tasks.filter { it.completedAt != null && it.completedAt!! in dayStart until dayEnd }
                .sumOf { it.endDate - it.startDate }

            val durationHours = durationMillis.toFloat() / (1000 * 60 * 60)

            entries.add(BarEntry(index.toFloat(), durationHours))
            labels.add(label)
        }

        _barEntries.postValue(entries)
        _xAxisLabels.postValue(labels)
    }





    // Process monthly tasks durations into bar entries
    private fun processMonthlyTasksDuration(tasks: List<Task>) {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault()) // Month abbreviation
        
        // Get the last 5 months (or the specified analysis period)
        val months = mutableListOf<Calendar>()
        for (i in 4 downTo 0) {
            val monthCalendar = Calendar.getInstance()
            monthCalendar.add(Calendar.MONTH, -i)
            months.add(monthCalendar)
        }
        
        // Calculate average task duration in each month
        val monthDurationMap = mutableMapOf<Int, Float>()
        val monthTaskCounts = mutableMapOf<Int, Int>()
        val monthLabels = mutableListOf<String>()
        
        months.forEachIndexed { index, month ->
            monthLabels.add(monthFormat.format(month.time))
            monthDurationMap[index] = 0f
            monthTaskCounts[index] = 0
            
            // Calculate total duration of tasks completed in this month
            tasks.forEach { task ->
                task.completedAt?.let { completedAt ->
                    val taskMonth = Calendar.getInstance()
                    taskMonth.timeInMillis = completedAt
                    
                    if (isSameMonth(taskMonth, month)) {
                        // Calculate task duration in hours
                        val durationInHours = (task.completedAt!! - task.startDate) / (1000 * 60 * 60f)
                        monthDurationMap[index] = (monthDurationMap[index] ?: 0f) + durationInHours
                        monthTaskCounts[index] = (monthTaskCounts[index] ?: 0) + 1
                    }
                }
            }
        }
        
        // Calculate average durations and create bar entries
        val entries = months.indices.map { index ->
            val taskCount = monthTaskCounts[index] ?: 0
            val averageDuration = if (taskCount > 0) {
                (monthDurationMap[index] ?: 0f) / taskCount
            } else {
                0f
            }
            BarEntry(index.toFloat(), averageDuration)
        }
        
        // Update LiveData
        _barEntries.postValue(entries)
        _xAxisLabels.postValue(monthLabels)
    }
    
    // Process yearly tasks durations into bar entries
    private fun processYearlyTasksDuration(tasks: List<Task>) {
        val calendar = Calendar.getInstance()
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        
        // Get the last 5 years (or the specified analysis period)
        val years = mutableListOf<Calendar>()
        for (i in 4 downTo 0) {
            val yearCalendar = Calendar.getInstance()
            yearCalendar.add(Calendar.YEAR, -i)
            years.add(yearCalendar)
        }
        
        // Calculate average task duration in each year
        val yearDurationMap = mutableMapOf<Int, Float>()
        val yearTaskCounts = mutableMapOf<Int, Int>()
        val yearLabels = mutableListOf<String>()
        
        years.forEachIndexed { index, year ->
            yearLabels.add(yearFormat.format(year.time))
            yearDurationMap[index] = 0f
            yearTaskCounts[index] = 0
            
            // Calculate total duration of tasks completed in this year
            tasks.forEach { task ->
                task.completedAt?.let { completedAt ->
                    val taskYear = Calendar.getInstance()
                    taskYear.timeInMillis = completedAt
                    
                    if (isSameYear(taskYear, year)) {
                        // Calculate task duration in hours
                        val durationInHours = (task.completedAt!! - task.startDate) / (1000 * 60 * 60f)
                        yearDurationMap[index] = (yearDurationMap[index] ?: 0f) + durationInHours
                        yearTaskCounts[index] = (yearTaskCounts[index] ?: 0) + 1
                    }
                }
            }
        }
        
        // Calculate average durations and create bar entries
        val entries = years.indices.map { index ->
            val taskCount = yearTaskCounts[index] ?: 0
            val averageDuration = if (taskCount > 0) {
                (yearDurationMap[index] ?: 0f) / taskCount
            } else {
                0f
            }
            BarEntry(index.toFloat(), averageDuration)
        }
        
        // Update LiveData
        _barEntries.postValue(entries)
        _xAxisLabels.postValue(yearLabels)
    }
    
    // Helper functions for date comparison
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun isSameMonth(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }
    
    private fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }

    private fun getLastNDates(n: Int): List<Pair<String, Long>> {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("MMM d", Locale.getDefault()) // "May 23"
        return (0 until n).map {
            val dateLabel = format.format(calendar.time)
            val dateMillis = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            dateLabel to dateMillis
        }.reversed() // Show oldest to newest
    }

}
