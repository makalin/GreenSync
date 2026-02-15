package com.greensync.app.network

import com.greensync.app.BuildConfig
import com.greensync.app.model.CityInsightsResponse
import com.greensync.app.model.ForecastResponse
import com.greensync.app.model.Intersection
import com.greensync.app.model.RecommendationResponse
import com.greensync.app.model.SignalsResponse
import com.greensync.app.model.SimulationDetails
import com.greensync.app.model.SimulationRequest
import com.greensync.app.model.SimulationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class GreenSyncApi(private val baseUrl: String = BuildConfig.API_BASE_URL) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchRecommendation(speed: Double = 35.0): RecommendationResponse =
        withContext(Dispatchers.IO) {
            val endpoint = buildUrl("/api/recommendation", mapOf("speed" to speed.toString()))
            val responseBody = executeGet(endpoint)
            json.decodeFromString(RecommendationResponse.serializer(), responseBody)
        }

    suspend fun fetchSignals(limit: Int = 3): List<Intersection> = withContext(Dispatchers.IO) {
        val endpoint = buildUrl("/api/signals", mapOf("limit" to limit.toString()))
        val responseBody = executeGet(endpoint)
        val parsed = json.decodeFromString(SignalsResponse.serializer(), responseBody)
        parsed.intersections
    }

    suspend fun fetchCityInsights(): CityInsightsResponse = withContext(Dispatchers.IO) {
        val endpoint = buildUrl("/api/insights/cities")
        val responseBody = executeGet(endpoint)
        json.decodeFromString(CityInsightsResponse.serializer(), responseBody)
    }

    suspend fun fetchForecast(city: String? = null): ForecastResponse = withContext(Dispatchers.IO) {
        val params = city?.let { mapOf("city" to it) } ?: emptyMap()
        val endpoint = buildUrl("/api/routes/forecast", params)
        val responseBody = executeGet(endpoint)
        json.decodeFromString(ForecastResponse.serializer(), responseBody)
    }

    suspend fun simulateApproach(request: SimulationRequest): SimulationDetails = withContext(Dispatchers.IO) {
        val endpoint = URL("$baseUrl/api/simulations/approach")
        val responseBody = executePost(endpoint, json.encodeToString(SimulationRequest.serializer(), request))
        return@withContext json.decodeFromString(SimulationResponse.serializer(), responseBody).simulation
    }

    private fun buildUrl(path: String, params: Map<String, String> = emptyMap()): URL {
        val query = if (params.isEmpty()) {
            ""
        } else {
            params.entries.joinToString("&") { (key, value) ->
                val encodedValue = java.net.URLEncoder.encode(value, StandardCharsets.UTF_8)
                "$key=$encodedValue"
            }
        }
        val formatted = if (query.isEmpty()) "$baseUrl$path" else "$baseUrl$path?$query"
        return URL(formatted)
    }

    private fun executeGet(url: URL): String {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
        }

        connection.inputStream.use { stream ->
            BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
                return reader.readText()
            }
        }
    }

    private fun executePost(url: URL, body: String): String {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 5000
            readTimeout = 5000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        connection.outputStream.use { stream ->
            BufferedWriter(OutputStreamWriter(stream, StandardCharsets.UTF_8)).use { writer ->
                writer.write(body)
            }
        }

        connection.inputStream.use { stream ->
            BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
                return reader.readText()
            }
        }
    }
}
