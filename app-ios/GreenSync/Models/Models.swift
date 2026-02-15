import Foundation

struct LocationDTO: Codable {
    let latitude: Double
    let longitude: Double
}

struct PhaseTimelineItem: Codable {
    let color: String
    let start: Int
    let end: Int
    let duration: Int
}

struct PhaseInfo: Codable {
    let color: String
    let timeRemaining: Int
    let nextGreenIn: Int
    let timeline: [PhaseTimelineItem]
}

struct Intersection: Codable, Identifiable {
    let id: String
    let city: String
    let name: String
    let latitude: Double
    let longitude: Double
    let speedLimitKph: Double?
    let approachDistanceMeters: Double?
    let phase: PhaseInfo?
    let updatedAt: TimeInterval?
    let distanceMeters: Double?
}

struct Suggestion: Codable {
    let intersectionId: String
    let suggestedSpeedKph: Double
    let obeyLimit: Double
    let phase: String
    let phaseEndsIn: Int
    let nextGreenIn: Int
    let distanceMeters: Double
}

struct RecommendationResponse: Codable {
    let location: LocationDTO
    let speed: Double
    let suggestion: Suggestion
    let intersection: Intersection
}

struct SignalsResponse: Codable {
    let location: LocationDTO
    let intersections: [Intersection]
}

struct AdvisoryViewData {
    let recommendedSpeed: Double
    let limit: Double
    let phase: String
    let phaseEndsIn: Int
    let nextGreenIn: Int
    let nearestIntersection: Intersection?
}

struct CityInsightsResponse: Codable {
    let generatedAt: TimeInterval
    let insights: [CityInsight]
}

struct CityInsight: Codable, Identifiable {
    var id: String { cityCode }
    let city: String
    let cityCode: String
    let totalIntersections: Int
    let averageDelaySeconds: Double
    let corridors: [String]
    let nextSignal: InsightSignal?
}

struct InsightSignal: Codable {
    let id: String
    let name: String
    let phase: PhaseInfo
}

struct ForecastResponse: Codable {
    let city: String
    let generatedAt: TimeInterval
    let forecast: [ForecastItem]
}

struct ForecastItem: Codable, Identifiable {
    var id: String { intersectionId }
    let intersectionId: String
    let city: String
    let corridor: String?
    let recommendation: Suggestion
}

struct SimulationResponse: Codable {
    let simulation: SimulationDetails
}

struct SimulationDetails: Codable {
    let intersection: SimulationIntersection
    let startSpeedKph: Double
    let driverLatencySeconds: Double
    let travelTimeSeconds: Double
    let phase: PhaseInfo
    let willCatchGreen: Bool
    let recommendedSpeedKph: Double
}

struct SimulationIntersection: Codable {
    let id: String
    let name: String
    let city: String
}
