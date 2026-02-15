import SwiftUI

struct ContentView: View {
    @ObservedObject var viewModel: AdvisorViewModel

    var body: some View {
        NavigationView {
            List {
                headerSection
                simulationSection
                if viewModel.isLoading {
                    loadingSection
                } else if let error = viewModel.errorMessage {
                    errorSection(error)
                } else {
                    signalsSection
                    insightsSection
                    forecastSection
                }
            }
            .navigationTitle("GreenSync")
            .toolbar {
                Button("Refresh") {
                    Task { await viewModel.refresh() }
                }
            }
        }
    }

    private var headerSection: some View {
        Section("Advisor") {
            if let advisory = viewModel.advisory {
                VStack(alignment: .leading, spacing: 8) {
                    Text("Recommended speed")
                        .font(.caption)
                    Text("\(Int(advisory.recommendedSpeed)) km/h")
                        .font(.system(size: 40, weight: .bold))
                    Text("Limit \(Int(advisory.limit)) km/h · Phase \(advisory.phase)")
                    Text("Next green in \(advisory.nextGreenIn)s · Phase ends in \(advisory.phaseEndsIn)s")
                        .font(.caption)
                    if let intersection = advisory.nearestIntersection {
                        Text("Approaching \(intersection.name) (\(intersection.city))")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                }
                .padding(.vertical, 4)
            } else {
                Text("Calculating optimal speed…")
            }
        }
    }

    private var simulationSection: some View {
        Section("Simulator") {
            if let simulation = viewModel.simulation {
                VStack(alignment: .leading, spacing: 8) {
                    Text("Approach \(simulation.intersection.name)")
                        .font(.headline)
                    Text("Start \(Int(simulation.startSpeedKph)) km/h · Travel time \(String(format: \"%.1f\", simulation.travelTimeSeconds))s")
                    Text(simulation.willCatchGreen ? "Stay in the green wave" : "Adjust speed to catch the wave")
                        .foregroundStyle(simulation.willCatchGreen ? .green : .orange)
                    Text("Recommended \(Int(simulation.recommendedSpeedKph)) km/h")
                        .font(.caption)
                }
                .padding(.vertical, 4)
            } else {
                Text("Preparing simulator…")
            }
        }
    }

    private var loadingSection: some View {
        Section {
            HStack {
                ProgressView()
                Text("Syncing with city signals")
            }
        }
    }

    private func errorSection(_ message: String) -> some View {
        Section {
            VStack(alignment: .leading, spacing: 8) {
                Text(message)
                    .foregroundStyle(.red)
                Button("Retry") {
                    Task { await viewModel.refresh() }
                }
            }
        }
    }

    private var signalsSection: some View {
        Section("Upcoming Signals") {
            ForEach(viewModel.intersections) { signal in
                VStack(alignment: .leading) {
                    Text(signal.name)
                        .font(.headline)
                    Text(signal.city)
                        .font(.subheadline)
                    if let phase = signal.phase {
                        Text("\(phase.color) – \(phase.timeRemaining)s remaining")
                            .font(.caption)
                    }
                    if let distance = signal.distanceMeters {
                        Text(String(format: "%.0f m away", distance))
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                    }
                }
                .padding(.vertical, 4)
            }
        }
    }

    private var insightsSection: some View {
        Section("City Insights") {
            ForEach(viewModel.insights) { insight in
                VStack(alignment: .leading, spacing: 4) {
                    Text(insight.city)
                        .font(.headline)
                    Text("\(insight.totalIntersections) connected intersections · Avg delay \(insight.averageDelaySeconds, specifier: \"%.1f\")s")
                        .font(.caption)
                    if let next = insight.nextSignal {
                        Text("Next: \(next.name) – \(next.phase.color) in \(next.phase.nextGreenIn)s")
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                    }
                }
                .padding(.vertical, 4)
            }
        }
    }

    private var forecastSection: some View {
        Section("Route Forecast") {
            ForEach(viewModel.forecast) { item in
                VStack(alignment: .leading, spacing: 4) {
                    Text(item.corridor ?? item.intersectionId)
                        .font(.headline)
                    Text(item.city)
                        .font(.subheadline)
                    Text("Suggested \(Int(item.recommendation.suggestedSpeedKph)) km/h")
                        .font(.caption)
                    Text("Phase \(item.recommendation.phase)")
                        .font(.caption2)
                }
                .padding(.vertical, 4)
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(viewModel: AdvisorViewModel())
    }
}
