const EARTH_RADIUS_METERS = 6371000;

export function toRadians(value) {
  return (value * Math.PI) / 180;
}

export function haversineDistance(a, b) {
  const dLat = toRadians(b.latitude - a.latitude);
  const dLon = toRadians(b.longitude - a.longitude);
  const lat1 = toRadians(a.latitude);
  const lat2 = toRadians(b.latitude);

  const h =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(lat1) * Math.cos(lat2) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);

  const c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
  return EARTH_RADIUS_METERS * c;
}

export function cyclePosition(intersection, timestampMs = Date.now()) {
  const elapsed = Math.floor(timestampMs / 1000) + intersection.offsetSeconds;
  return elapsed % intersection.cycleSeconds;
}

export function expandCycle(phases) {
  const timeline = [];
  let cursor = 0;
  phases.forEach((phase) => {
    timeline.push({ ...phase, start: cursor, end: cursor + phase.duration });
    cursor += phase.duration;
  });
  return timeline;
}

export function getPhaseInfo(intersection, timestampMs = Date.now()) {
  const timeline = expandCycle(intersection.phases);
  const position = cyclePosition(intersection, timestampMs);
  const current = timeline.find((phase) => position >= phase.start && position < phase.end);
  const timeIntoPhase = position - current.start;
  const timeRemaining = current.duration - timeIntoPhase;
  const nextGreen = timeline.find((phase) => phase.color === 'GREEN' && phase.start >= position);
  const timeUntilNextGreen = nextGreen
    ? nextGreen.start - position
    : intersection.cycleSeconds - position + timeline.find((p) => p.color === 'GREEN').start;

  return {
    color: current.color,
    timeRemaining,
    nextGreenIn: current.color === 'GREEN' ? 0 : timeUntilNextGreen,
    timeline
  };
}

export function recommendSpeed(intersection, currentSpeedKph = 0, timestampMs = Date.now()) {
  const phaseInfo = getPhaseInfo(intersection, timestampMs);
  const distance = intersection.approachDistanceMeters;
  const bufferSeconds = 4;

  let targetSpeedKph;
  if (phaseInfo.color === 'GREEN' && phaseInfo.timeRemaining > distance / (currentSpeedKph / 3.6 + 0.1)) {
    // Maintain speed but don't exceed limit
    targetSpeedKph = Math.min(Math.max(currentSpeedKph, 25), intersection.speedLimitKph);
  } else if (phaseInfo.nextGreenIn > 0) {
    const timeAvailable = phaseInfo.nextGreenIn + bufferSeconds;
    targetSpeedKph = Math.min((distance / timeAvailable) * 3.6, intersection.speedLimitKph);
  } else {
    targetSpeedKph = Math.min(intersection.speedLimitKph, Math.max(25, currentSpeedKph));
  }

  return {
    intersectionId: intersection.id,
    suggestedSpeedKph: Number(targetSpeedKph.toFixed(1)),
    obeyLimit: intersection.speedLimitKph,
    phase: phaseInfo.color,
    phaseEndsIn: phaseInfo.timeRemaining,
    nextGreenIn: phaseInfo.nextGreenIn,
    distanceMeters: distance
  };
}
