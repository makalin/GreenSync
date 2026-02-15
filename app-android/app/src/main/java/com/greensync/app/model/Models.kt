package com.greensync.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class PhaseTimelineItem(
    val color: String,
    val start: Int,
    val end: Int,
    val duration: Int
)

@Serializable
data class PhaseInfo(
    val color: String,
    @SerialName("timeRemaining") val timeRemaining: Int,
    @SerialName("nextGreenIn") val nextGreenIn: Int,
    val timeline: List<PhaseTimelineItem> = emptyList()
)

@Serializable
data class Intersection(
    val id: String,
    val city: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("speedLimitKph") val speedLimitKph: Double? = null,
    @SerialName("approachDistanceMeters") val approachDistanceMeters: Double? = null,
    val phase: PhaseInfo? = null,
    @SerialName("updatedAt") val updatedAt: Long? = null,
    @SerialName("distanceMeters") val distanceMeters: Double? = null
)

@Serializable
data class Suggestion(
    @SerialName("intersectionId") val intersectionId: String,
    @SerialName("suggestedSpeedKph") val suggestedSpeedKph: Double,
    @SerialName("obeyLimit") val obeyLimit: Double,
    val phase: String,
    @SerialName("phaseEndsIn") val phaseEndsIn: Int,
    @SerialName("nextGreenIn") val nextGreenIn: Int,
    @SerialName("distanceMeters") val distanceMeters: Double
)

@Serializable
data class RecommendationResponse(
    val location: LocationDto,
    val speed: Double,
    val suggestion: Suggestion,
    val intersection: Intersection
)

@Serializable
data class SignalsResponse(
    val location: LocationDto,
    val intersections: List<Intersection>
)

@Serializable
data class AdvisoryUiModel(
    val recommendedSpeed: Double,
    val limit: Double,
    val phase: String,
    val phaseEndsIn: Int,
    val nextGreenIn: Int,
    val nearestIntersection: Intersection?
)

@Serializable
data class CityInsight(
    val city: String,
    @SerialName("cityCode") val cityCode: String,
    @SerialName("totalIntersections") val totalIntersections: Int,
    @SerialName("averageDelaySeconds") val averageDelaySeconds: Double,
    val corridors: List<String> = emptyList(),
    @SerialName("nextSignal") val nextSignal: InsightSignal?
)

@Serializable
data class InsightSignal(
    val id: String,
    val name: String,
    val phase: PhaseInfo
)

@Serializable
data class CityInsightsResponse(
    @SerialName("generatedAt") val generatedAt: Long,
    val insights: List<CityInsight>
)

@Serializable
data class ForecastItem(
    @SerialName("intersectionId") val intersectionId: String,
    val city: String,
    val corridor: String? = null,
    val recommendation: Suggestion
)

@Serializable
data class ForecastResponse(
    val city: String,
    @SerialName("generatedAt") val generatedAt: Long,
    val forecast: List<ForecastItem>
)

@Serializable
data class SimulationRequest(
    @SerialName("intersectionId") val intersectionId: String,
    @SerialName("startSpeedKph") val startSpeedKph: Double,
    @SerialName("driverLatencySeconds") val driverLatencySeconds: Double
)

@Serializable
data class SimulationDetails(
    val intersection: SimulationIntersection,
    @SerialName("startSpeedKph") val startSpeedKph: Double,
    @SerialName("driverLatencySeconds") val driverLatencySeconds: Double,
    @SerialName("travelTimeSeconds") val travelTimeSeconds: Double,
    val phase: PhaseInfo,
    @SerialName("willCatchGreen") val willCatchGreen: Boolean,
    @SerialName("recommendedSpeedKph") val recommendedSpeedKph: Double
)

@Serializable
data class SimulationIntersection(
    val id: String,
    val name: String,
    val city: String
)

@Serializable
data class SimulationResponse(
    val simulation: SimulationDetails
)
