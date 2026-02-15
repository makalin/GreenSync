import Foundation

@MainActor
final class AdvisorViewModel: ObservableObject {
    @Published private(set) var advisory: AdvisoryViewData?
    @Published private(set) var intersections: [Intersection] = []
    @Published private(set) var isLoading = false
    @Published var errorMessage: String?
    @Published private(set) var insights: [CityInsight] = []
    @Published private(set) var forecast: [ForecastItem] = []
    @Published private(set) var simulation: SimulationDetails?

    private let api = GreenSyncAPI.shared

    init() {
        Task { await refresh() }
    }

    func refresh(speed: Double = 40) async {
        isLoading = true
        errorMessage = nil
        do {
            async let recommendation = api.fetchRecommendation(speed: speed)
            async let signals = api.fetchSignals()
            async let insightsResponse = api.fetchCityInsights()
            let (recommendationResponse, intersectionsResponse, cityInsights) = try await (recommendation, signals, insightsResponse)

            intersections = intersectionsResponse
            advisory = AdvisoryViewData(
                recommendedSpeed: recommendationResponse.suggestion.suggestedSpeedKph,
                limit: recommendationResponse.suggestion.obeyLimit,
                phase: recommendationResponse.suggestion.phase,
                phaseEndsIn: recommendationResponse.suggestion.phaseEndsIn,
                nextGreenIn: recommendationResponse.suggestion.nextGreenIn,
                nearestIntersection: intersectionsResponse.first ?? recommendationResponse.intersection
            )
            insights = cityInsights.insights

            let activeCity = advisory?.nearestIntersection?.city
            forecast = try await api.fetchForecast(city: activeCity).forecast
            simulation = try await api.simulateApproach(
                intersectionId: recommendationResponse.intersection.id,
                startSpeed: recommendationResponse.suggestion.suggestedSpeedKph
            )
        } catch {
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}
