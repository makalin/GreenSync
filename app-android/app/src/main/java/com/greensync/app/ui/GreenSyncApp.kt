package com.greensync.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.greensync.app.model.CityInsight
import com.greensync.app.model.ForecastItem
import com.greensync.app.model.SimulationDetails
import kotlin.math.roundToInt

@Composable
fun GreenSyncApp(viewModel: GreenSyncViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderSection(state)
            SimulationSection(state.simulation)
            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(message = state.error, onRetry = { viewModel.refresh() })
                else -> {
                    SignalsList(state)
                    InsightsSection(state.insights)
                    ForecastSection(state.forecast)
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(state: GreenSyncViewModel.UiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Green Wave Advisor", style = MaterialTheme.typography.titleLarge)
            val advisory = state.advisory
            if (advisory != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Recommended speed")
                        Text(
                            text = "${advisory.recommendedSpeed.roundToInt()} km/h",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Limit ${advisory.limit.roundToInt()} km/h")
                        Text(text = "Phase ${advisory.phase}")
                    }
                }
                Text(
                    text = "Next green in ${advisory.nextGreenIn}s • Phase ends in ${advisory.phaseEndsIn}s",
                    modifier = Modifier.padding(top = 8.dp)
                )
                advisory.nearestIntersection?.let {
                    Text(text = "Approaching ${it.name} (${it.city})", modifier = Modifier.padding(top = 4.dp))
                }
            } else {
                Text(text = "Fetching optimal speed…", modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(text = "Syncing with signals", modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Retry")
        }
    }
}

@Composable
private fun SignalsList(state: GreenSyncViewModel.UiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        state.signals.forEach { signal ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = signal.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = signal.city, style = MaterialTheme.typography.bodyMedium)
                    signal.phase?.let {
                        Text(
                            text = "${it.color} – ${it.timeRemaining}s remaining",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    signal.distanceMeters?.let {
                        Text(text = "${it.roundToInt()} m away", modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightsSection(insights: List<CityInsight>) {
    if (insights.isEmpty()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "City Insights", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
            insights.forEach { insight ->
                ElevatedCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = insight.city, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${insight.totalIntersections} smart intersections")
                        Text(text = "Avg delay ${insight.averageDelaySeconds}s")
                        insight.nextSignal?.let {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(text = "Next signal: ${it.name}")
                            Text(text = "${it.phase.color} in ${it.phase.nextGreenIn}s", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastSection(forecast: List<ForecastItem>) {
    if (forecast.isEmpty()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Route Forecast", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
            forecast.forEach { item ->
                ElevatedCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = item.corridor ?: item.intersectionId, style = MaterialTheme.typography.titleMedium)
                        Text(text = item.city, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Suggested ${item.recommendation.suggestedSpeedKph.roundToInt()} km/h")
                        Text(
                            text = "Phase ${item.recommendation.phase} · Next green in ${item.recommendation.nextGreenIn}s",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SimulationSection(simulation: SimulationDetails?) {
    if (simulation == null) return
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Approach Simulator", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Start ${simulation.startSpeedKph.roundToInt()} km/h • Travel time ${simulation.travelTimeSeconds}s",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = if (simulation.willCatchGreen) "You will stay in the green wave." else "Slow down to rejoin the green wave.",
                color = if (simulation.willCatchGreen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Recommend ${simulation.recommendedSpeedKph.roundToInt()} km/h for ${simulation.intersection.name}",
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
