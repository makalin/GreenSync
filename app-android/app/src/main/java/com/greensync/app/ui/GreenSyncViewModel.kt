package com.greensync.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greensync.app.model.AdvisoryUiModel
import com.greensync.app.model.CityInsight
import com.greensync.app.model.ForecastItem
import com.greensync.app.model.Intersection
import com.greensync.app.network.GreenSyncApi
import com.greensync.app.model.SimulationDetails
import com.greensync.app.model.SimulationRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GreenSyncViewModel(
    private val api: GreenSyncApi = GreenSyncApi()
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val advisory: AdvisoryUiModel? = null,
        val signals: List<Intersection> = emptyList(),
        val insights: List<CityInsight> = emptyList(),
        val forecast: List<ForecastItem> = emptyList(),
        val simulation: SimulationDetails? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh(speed: Double = 40.0) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val recommendationDeferred = async { api.fetchRecommendation(speed) }
                val intersectionsDeferred = async { api.fetchSignals() }
                val insightsDeferred = async { api.fetchCityInsights() }

                val advisoryResponse = recommendationDeferred.await()
                val intersections = intersectionsDeferred.await()
                val advisory = AdvisoryUiModel(
                    recommendedSpeed = advisoryResponse.suggestion.suggestedSpeedKph,
                    limit = advisoryResponse.suggestion.obeyLimit,
                    phase = advisoryResponse.suggestion.phase,
                    phaseEndsIn = advisoryResponse.suggestion.phaseEndsIn,
                    nextGreenIn = advisoryResponse.suggestion.nextGreenIn,
                    nearestIntersection = intersections.firstOrNull()
                        ?: advisoryResponse.intersection
                )

                val insightsResponse = insightsDeferred.await()
                val activeCity = advisory.nearestIntersection?.city
                val forecastResponse = api.fetchForecast(activeCity)

                val simulation = api.simulateApproach(
                    SimulationRequest(
                        intersectionId = advisoryResponse.intersection.id,
                        startSpeedKph = advisoryResponse.suggestion.suggestedSpeedKph,
                        driverLatencySeconds = 1.6
                    )
                )

                _state.value = UiState(
                    isLoading = false,
                    advisory = advisory,
                    signals = intersections,
                    insights = insightsResponse.insights,
                    forecast = forecastResponse.forecast,
                    simulation = simulation,
                    error = null
                )
            } catch (ex: Exception) {
                _state.value = UiState(
                    isLoading = false,
                    advisory = _state.value.advisory,
                    signals = _state.value.signals,
                    insights = _state.value.insights,
                    forecast = _state.value.forecast,
                    simulation = _state.value.simulation,
                    error = ex.message ?: "Unable to load data"
                )
            }
        }
    }
}
