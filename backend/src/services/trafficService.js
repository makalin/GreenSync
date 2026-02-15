import { intersections } from '../data/intersections.js';
import { haversineDistance, recommendSpeed, getPhaseInfo } from '../utils.js';

export function nearestIntersections(location, limit = 3, timestamp = Date.now()) {
  return intersections
    .map((intersection) => {
      const distanceMeters = haversineDistance(location, intersection);
      return {
        ...intersection,
        distanceMeters,
        phase: getPhaseInfo(intersection, timestamp),
        updatedAt: timestamp,
      };
    })
    .sort((a, b) => a.distanceMeters - b.distanceMeters)
    .slice(0, limit);
}

export function summarizeCities(timestamp = Date.now()) {
  const grouped = intersections.reduce((acc, intersection) => {
    const key = intersection.cityCode ?? intersection.city;
    if (!acc[key]) {
      acc[key] = {
        city: intersection.city,
        cityCode: intersection.cityCode ?? intersection.city.substring(0, 3).toUpperCase(),
        totalIntersections: 0,
        averageDelay: 0,
        corridors: new Set(),
        samples: [],
      };
    }
    acc[key].totalIntersections += 1;
    acc[key].averageDelay += intersection.avgDelaySeconds ?? 0;
    if (intersection.corridor) {
      acc[key].corridors.add(intersection.corridor);
    }
    acc[key].samples.push({
      id: intersection.id,
      name: intersection.name,
      phase: getPhaseInfo(intersection, timestamp),
    });
    return acc;
  }, {});

  return Object.values(grouped).map((item) => {
    const sortedSamples = item.samples.sort(
      (a, b) => a.phase.nextGreenIn - b.phase.nextGreenIn
    );
    const nextSignal = sortedSamples[0];
    const avgDelay = item.totalIntersections
      ? Number((item.averageDelay / item.totalIntersections).toFixed(1))
      : 0;
    return {
      city: item.city,
      cityCode: item.cityCode,
      totalIntersections: item.totalIntersections,
      corridors: Array.from(item.corridors),
      averageDelaySeconds: avgDelay,
      nextSignal,
    };
  });
}

export function forecastRoute(cityFilter, limit = 3, timestamp = Date.now()) {
  const filtered = cityFilter
    ? intersections.filter((i) => i.city.toLowerCase() === cityFilter.toLowerCase())
    : intersections;

  return filtered
    .slice(0, limit)
    .map((intersection) => {
      const suggestion = recommendSpeed(intersection, intersection.speedLimitKph * 0.7, timestamp);
      return {
        intersectionId: intersection.id,
        city: intersection.city,
        corridor: intersection.corridor,
        recommendation: suggestion,
      };
    });
}

export function simulateApproach({
  intersectionId,
  startSpeedKph = 40,
  driverLatencySeconds = 1.5,
  timestamp = Date.now(),
}) {
  const intersection = intersections.find((i) => i.id === intersectionId);
  if (!intersection) {
    throw new Error(`Unknown intersection ${intersectionId}`);
  }
  const phase = getPhaseInfo(intersection, timestamp);
  const suggestion = recommendSpeed(intersection, startSpeedKph, timestamp);
  const travelTimeSeconds = intersection.approachDistanceMeters / ((startSpeedKph / 3.6) || 1);
  const etaPhase = phase.timeRemaining - travelTimeSeconds - driverLatencySeconds;

  return {
    intersection: {
      id: intersection.id,
      name: intersection.name,
      city: intersection.city,
    },
    startSpeedKph,
    driverLatencySeconds,
    travelTimeSeconds: Number(travelTimeSeconds.toFixed(1)),
    willCatchGreen: etaPhase >= 0,
    recommendedSpeedKph: suggestion.suggestedSpeedKph,
    phase,
  };
}
