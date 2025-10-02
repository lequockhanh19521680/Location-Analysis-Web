import { Client } from '@googlemaps/google-maps-services-js';
import { normalizeScore } from '../utils/scoring';

const client = new Client({});
const GOOGLE_MAPS_API_KEY = process.env.GOOGLE_MAPS_API_KEY || '';

export interface PillarBResult {
  score: number;
  subScores: {
    b1: number; // Foot Traffic Estimation
    b2: number; // Public Transport Access
    b3: number; // Vehicle Accessibility
    b4: number; // Visibility Score
  };
  rawData: any;
}

/**
 * Pillar B: Traffic & Accessibility Analysis
 */
export const analyzePillarB = async (
  latitude: number,
  longitude: number,
  radius: number
): Promise<PillarBResult> => {
  try {
    // B.1: Foot Traffic Estimation - Based on nearby POIs
    const poiSearch = await client.placesNearby({
      params: {
        location: { lat: latitude, lng: longitude },
        radius,
        key: GOOGLE_MAPS_API_KEY,
      },
    });
    
    const totalPOIs = poiSearch.data.results?.length || 0;
    // More POIs = more foot traffic
    const subScoreB1 = normalizeScore(totalPOIs, 0, 50, false);
    
    // B.2: Public Transport Access - Transit stations nearby
    const transitSearch = await client.placesNearby({
      params: {
        location: { lat: latitude, lng: longitude },
        radius: 500, // 500m for transit
        type: 'transit_station',
        key: GOOGLE_MAPS_API_KEY,
      },
    });
    
    const transitCount = transitSearch.data.results?.length || 0;
    const subScoreB2 = normalizeScore(transitCount, 0, 5, false);
    
    // B.3: Vehicle Accessibility - Parking availability
    const parkingSearch = await client.placesNearby({
      params: {
        location: { lat: latitude, lng: longitude },
        radius,
        type: 'parking',
        key: GOOGLE_MAPS_API_KEY,
      },
    });
    
    const parkingCount = parkingSearch.data.results?.length || 0;
    const subScoreB3 = normalizeScore(parkingCount, 0, 10, false);
    
    // B.4: Visibility Score - Based on nearby major roads/landmarks
    // Simulated: higher if location is near main roads
    const visibilityScore = (subScoreB1 + subScoreB2) / 2;
    const subScoreB4 = Math.min(10, visibilityScore + 2); // Bonus for visibility
    
    const pillarScore = (subScoreB1 + subScoreB2 + subScoreB3 + subScoreB4) / 4;
    
    return {
      score: Math.round(pillarScore * 100) / 100,
      subScores: {
        b1: subScoreB1,
        b2: subScoreB2,
        b3: subScoreB3,
        b4: subScoreB4,
      },
      rawData: {
        totalPOIs,
        transitCount,
        parkingCount,
        nearbyTransit: transitSearch.data.results?.slice(0, 3).map(p => ({
          name: p.name,
          types: p.types,
        })),
      },
    };
  } catch (error) {
    console.error('Pillar B analysis error:', error);
    return {
      score: 5.0,
      subScores: { b1: 5.0, b2: 5.0, b3: 5.0, b4: 5.0 },
      rawData: { error: 'Failed to fetch data' },
    };
  }
};
