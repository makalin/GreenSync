import Foundation

final class GreenSyncAPI {
    static let shared = GreenSyncAPI()
    private init() {}

    private let baseURL = URL(string: "http://localhost:4000")!

    func fetchRecommendation(speed: Double = 40) async throws -> RecommendationResponse {
        let url = baseURL.appending(path: "api/recommendation")
        var components = URLComponents(url: url, resolvingAgainstBaseURL: false)
        components?.queryItems = [URLQueryItem(name: "speed", value: String(speed))]
        guard let finalURL = components?.url else { throw URLError(.badURL) }
        let (data, response) = try await URLSession.shared.data(from: finalURL)
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            throw URLError(.badServerResponse)
        }
        return try JSONDecoder().decode(RecommendationResponse.self, from: data)
    }

    func fetchSignals(limit: Int = 3) async throws -> [Intersection] {
        let url = baseURL.appending(path: "api/signals")
        var components = URLComponents(url: url, resolvingAgainstBaseURL: false)
        components?.queryItems = [URLQueryItem(name: "limit", value: String(limit))]
        guard let finalURL = components?.url else { throw URLError(.badURL) }
        let (data, response) = try await URLSession.shared.data(from: finalURL)
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            throw URLError(.badServerResponse)
        }
        let parsed = try JSONDecoder().decode(SignalsResponse.self, from: data)
        return parsed.intersections
    }

    func fetchCityInsights() async throws -> CityInsightsResponse {
        let url = baseURL.appending(path: "api/insights/cities")
        let (data, response) = try await URLSession.shared.data(from: url)
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            throw URLError(.badServerResponse)
        }
        return try JSONDecoder().decode(CityInsightsResponse.self, from: data)
    }

    func fetchForecast(city: String?) async throws -> ForecastResponse {
        var url = baseURL.appending(path: "api/routes/forecast")
        if let city {
            var components = URLComponents(url: url, resolvingAgainstBaseURL: false)
            components?.queryItems = [URLQueryItem(name: "city", value: city)]
            url = components?.url ?? url
        }
        let (data, response) = try await URLSession.shared.data(from: url)
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            throw URLError(.badServerResponse)
        }
        return try JSONDecoder().decode(ForecastResponse.self, from: data)
    }

    func simulateApproach(intersectionId: String, startSpeed: Double) async throws -> SimulationDetails {
        var request = URLRequest(url: baseURL.appending(path: "api/simulations/approach"))
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        let body: [String: Any] = [
            "intersectionId": intersectionId,
            "startSpeedKph": startSpeed,
            "driverLatencySeconds": 1.6
        ]
        request.httpBody = try JSONSerialization.data(withJSONObject: body)

        let (data, response) = try await URLSession.shared.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            throw URLError(.badServerResponse)
        }
        return try JSONDecoder().decode(SimulationResponse.self, from: data).simulation
    }
}
