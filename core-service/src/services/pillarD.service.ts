import { Client } from '@googlemaps/google-maps-services-js';
import { normalizeScore } from '../utils/scoring';

const client = new Client({});
const GOOGLE_MAPS_API_KEY = process.env.GOOGLE_MAPS_API_KEY || '';

export interface PillarDResult {
  score: number;
  subScores: {
    d1: number; // Parking Availability
    d2: number; // Safety Score
    d3: number; // Aesthetic Score
  };
  rawData: any;
}

/**
 * Pillar D: Infrastructure & Environment Analysis
 */
export const analyzePillarD = async (
  latitude: number,
  longitude: number,
  radius: number
): Promise<PillarDResult> => {
  try {
    // D.1: Parking Availability
    const parkingSearch = await client.placesNearby({
      params: {
        location: { lat: latitude, lng: longitude },
        radius,
        type: 'parking',
        key: GOOGLE_MAPS_API_KEY,
      },
    });
    
    const parkingCount = parkingSearch.data.results?.length || 0;
    const subScoreD1 = normalizeScore(parkingCount, 0, 10, false);
    
    // D.2: Safety Score - Police stations, hospitals nearby
    const safetySearch = await client.placesNearby({
      params: {
        location: { lat: latitude, lng: longitude },
        radius: 1000,
        type: 'police',
        key: GOOGLE_MAPS_API_KEY,
      },
    });
    
    const safetyFacilities = safetySearch.data.results?.length || 0;
    const subScoreD2 = normalizeScore(safetyFacilities, 0, 3, false);
    
    // D.3: Aesthetic Score - Parks, greenery nearby
    const aestheticSearch = await client.placesNearby({
      params: {
        location: { lat: latitude, lng: longitude },
        radius,
        type: 'park',
        key: GOOGLE_MAPS_API_KEY,
      },
    });
    
    const parksCount = aestheticSearch.data.results?.length || 0;
    const subScoreD3 = normalizeScore(parksCount, 0, 5, false);
    
    const pillarScore = (subScoreD1 + subScoreD2 + subScoreD3) / 3;
    
    return {
      score: Math.round(pillarScore * 100) / 100,
      subScores: {
        d1: subScoreD1,
        d2: subScoreD2,
        d3: subScoreD3,
      },
      rawData: {
        parkingCount,
        safetyFacilities,
        parksCount,
        nearbyAmenities: {
          parking: parkingSearch.data.results?.slice(0, 3).map(p => p.name),
          safety: safetySearch.data.results?.slice(0, 2).map(p => p.name),
          parks: aestheticSearch.data.results?.slice(0, 2).map(p => p.name),
        },
      },
    };
  } catch (error) {
    console.error('Pillar D analysis error:', error);
    return {
      score: 5.0,
      subScores: { d1: 5.0, d2: 5.0, d3: 5.0 },
      rawData: { error: 'Failed to fetch data' },
    };
  }
};
