export const intersections = [
  {
    id: 'nyc-001',
    city: 'New York',
    cityCode: 'NYC',
    name: '5th Ave & W 45th St',
    corridor: 'Midtown 5th Ave',
    latitude: 40.75586,
    longitude: -73.97926,
    cycleSeconds: 90,
    offsetSeconds: 12,
    approachDistanceMeters: 240,
    speedLimitKph: 50,
    avgDelaySeconds: 18,
    phases: [
      { color: 'GREEN', duration: 35 },
      { color: 'YELLOW', duration: 5 },
      { color: 'RED', duration: 50 }
    ]
  },
  {
    id: 'nyc-002',
    city: 'New York',
    cityCode: 'NYC',
    name: '6th Ave & W 34th St',
    corridor: 'Herald Square',
    latitude: 40.75058,
    longitude: -73.98702,
    cycleSeconds: 95,
    offsetSeconds: 30,
    approachDistanceMeters: 280,
    speedLimitKph: 45,
    avgDelaySeconds: 25,
    phases: [
      { color: 'GREEN', duration: 40 },
      { color: 'YELLOW', duration: 5 },
      { color: 'RED', duration: 50 }
    ]
  },
  {
    id: 'la-101',
    city: 'Los Angeles',
    cityCode: 'LA',
    name: 'Sunset Blvd & N Highland Ave',
    corridor: 'Hollywood Bowl',
    latitude: 34.10191,
    longitude: -118.33819,
    cycleSeconds: 105,
    offsetSeconds: 22,
    approachDistanceMeters: 310,
    speedLimitKph: 55,
    avgDelaySeconds: 30,
    phases: [
      { color: 'GREEN', duration: 40 },
      { color: 'YELLOW', duration: 4 },
      { color: 'RED', duration: 61 }
    ]
  },
  {
    id: 'berlin-21',
    city: 'Berlin',
    cityCode: 'BER',
    name: 'Karl-Marx-Allee & Strausberger Platz',
    corridor: 'Karl-Marx Axis',
    latitude: 52.51547,
    longitude: 13.42868,
    cycleSeconds: 75,
    offsetSeconds: 5,
    approachDistanceMeters: 200,
    speedLimitKph: 45,
    avgDelaySeconds: 15,
    phases: [
      { color: 'GREEN', duration: 30 },
      { color: 'YELLOW', duration: 3 },
      { color: 'RED', duration: 42 }
    ]
  },
  {
    id: 'helsinki-14',
    city: 'Helsinki',
    cityCode: 'HEL',
    name: 'Mannerheimintie & Kaivokatu',
    corridor: 'Central Station Loop',
    latitude: 60.16982,
    longitude: 24.93845,
    cycleSeconds: 80,
    offsetSeconds: 15,
    approachDistanceMeters: 260,
    speedLimitKph: 40,
    avgDelaySeconds: 20,
    phases: [
      { color: 'GREEN', duration: 32 },
      { color: 'YELLOW', duration: 4 },
      { color: 'RED', duration: 44 }
    ]
  },
  {
    id: 'sf-77',
    city: 'San Francisco',
    cityCode: 'SF',
    name: 'Market St & 7th St',
    corridor: 'Market Street Transit Mall',
    latitude: 37.78042,
    longitude: -122.41096,
    cycleSeconds: 85,
    offsetSeconds: 18,
    approachDistanceMeters: 230,
    speedLimitKph: 35,
    avgDelaySeconds: 28,
    phases: [
      { color: 'GREEN', duration: 33 },
      { color: 'YELLOW', duration: 4 },
      { color: 'RED', duration: 48 }
    ]
  }
];
