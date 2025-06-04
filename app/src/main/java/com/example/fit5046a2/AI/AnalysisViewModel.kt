package com.example.fit5046a2.AI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

data class GraphState(
    val graphDescription: String = "",
    val graphRecommendation: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

//generate desc for analysis page
class AnalysisViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val geminiAPI = GeminiAPI()
    private val _graphState = MutableStateFlow(GraphState())
    val graphState: StateFlow<GraphState> = _graphState.asStateFlow()
    
    // Store current graph data (exposed as properties)
    var currentGraphType: String = ""
        private set
    var currentBarEntries: List<BarEntry> = emptyList()
        private set
    var currentXAxisLabels: List<String> = emptyList()
        private set

    fun updateGraphContext(graphType: String, graphData: String) {
        viewModelScope.launch {
            _graphState.value = _graphState.value.copy(isLoading = true)
            try {
                val prompt = """
                    Based on this graph data:
                    Type: $graphType
                    Data: $graphData

                    Please provide:
                    1. A brief description of what this graph shows
                    2. Key insights or recommendations based on the data

                    Format the response as JSON with 'description' and 'recommendation' fields.
                """.trimIndent()

                val response = geminiAPI.generateResponse(prompt, UserContext(currentPage = "analysis"))

                // Clean up the response by removing markdown code block markers
                val cleanedResponse = response
                    .replace("```json", "")
                    .replace("```", "")
                    .trim()
                
                // Try to parse response as JSON
                try {
                    val jsonResponse = JSONObject(cleanedResponse)
                    val description = jsonResponse.optString("description", "Analysis not available")
                    val recommendation = jsonResponse.optString("recommendation", "No recommendations available")
                    
                    _graphState.value = _graphState.value.copy(
                        graphDescription = description,
                        graphRecommendation = recommendation,
                        isLoading = false,
                        error = null
                    )
                } catch (e: Exception) {
                    // If JSON parsing fails, use the raw response but still clean it up
                    _graphState.value = _graphState.value.copy(
                        graphDescription = cleanedResponse,
                        graphRecommendation = "",
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _graphState.value = _graphState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun analyzeGraphData(barEntries: List<BarEntry>, xAxisLabels: List<String>, analysisType: String) {
        // Store current graph data
        currentGraphType = analysisType
        currentBarEntries = barEntries
        currentXAxisLabels = xAxisLabels
        
        // Convert bar entries to string format for analysis
        val graphDataString = barEntries.mapIndexed { index, entry ->
            "${xAxisLabels.getOrNull(index) ?: "Label$index"}: ${entry.y}"
        }.toString()
        
        // Update graph context with the formatted data
        updateGraphContext(analysisType, graphDataString)
    }
    
    fun getFormattedGraphData(): List<String> {
        return currentBarEntries.mapIndexed { index, entry ->
            "${currentXAxisLabels.getOrNull(index) ?: "Label$index"}: ${entry.y}"
        }
    }
}
