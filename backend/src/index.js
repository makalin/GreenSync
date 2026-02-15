import express from 'express';
import cors from 'cors';
import { intersections } from './data/intersections.js';
import {
  nearestIntersections,
  summarizeCities,
  forecastRoute,
  simulateApproach,
} from './services/trafficService.js';
import { haversineDistance, recommendSpeed, getPhaseInfo } from './utils.js';

const app = express();
app.use(cors());
app.use(express.json());

const DEFAULT_LOCATION = { latitude: intersections[0].latitude, longitude: intersections[0].longitude };

function getLocationFromRequest(req) {
  const latitude = Number(req.query.lat ?? req.body?.lat ?? DEFAULT_LOCATION.latitude);
  const longitude = Number(req.query.lon ?? req.body?.lon ?? DEFAULT_LOCATION.longitude);
  return { latitude, longitude };
}

function enrichIntersection(intersection, timestamp) {
  const phase = getPhaseInfo(intersection, timestamp);
  return {
    ...intersection,
    phase,
    updatedAt: timestamp,
  };
}

app.get('/health', (_req, res) => {
  res.json({ status: 'ok', service: 'greensync-backend' });
});

app.get('/api/cities', (_req, res) => {
  const cities = Array.from(new Set(intersections.map((i) => i.city)));
  res.json({ cities });
});

app.get('/api/signals', (req, res) => {
  const location = getLocationFromRequest(req);
  const limit = Number(req.query.limit ?? 3);
  const timestamp = Date.now();

  const decorated = nearestIntersections(location, limit, timestamp).map((intersection) => ({
    ...intersection,
    distanceMeters: Number(intersection.distanceMeters.toFixed(1)),
  }));

  res.json({ location, intersections: decorated });
});

app.get('/api/recommendation', (req, res) => {
  const location = getLocationFromRequest(req);
  const currentSpeedKph = Number(req.query.speed ?? req.body?.speed ?? 35);
  const timestamp = Date.now();

  const closest = intersections
    .map((intersection) => ({
      data: intersection,
      distance: haversineDistance(location, intersection),
    }))
    .sort((a, b) => a.distance - b.distance)[0];

  const suggestion = recommendSpeed(closest.data, currentSpeedKph, timestamp);
  res.json({ location, speed: currentSpeedKph, suggestion, intersection: enrichIntersection(closest.data, timestamp) });
});

app.get('/api/insights/cities', (_req, res) => {
  const timestamp = Date.now();
  const stats = summarizeCities(timestamp);
  res.json({ generatedAt: timestamp, insights: stats });
});

app.get('/api/routes/forecast', (req, res) => {
  const { city, limit = 3 } = req.query;
  const timestamp = Date.now();
  const forecast = forecastRoute(city, Number(limit), timestamp);
  res.json({ city: city ?? 'all', generatedAt: timestamp, forecast });
});

app.post('/api/simulations/approach', (req, res) => {
  try {
    const { intersectionId } = req.body;
    if (!intersectionId) {
      return res.status(400).json({ error: 'intersectionId is required' });
    }
    const payload = {
      intersectionId,
      startSpeedKph: Number(req.body.startSpeedKph ?? 40),
      driverLatencySeconds: Number(req.body.driverLatencySeconds ?? 1.5),
    };
    const simulation = simulateApproach(payload);
    res.json({ simulation });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
  console.log(`GreenSync backend listening on port ${PORT}`);
});
