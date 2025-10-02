import { Client, PlaceInputType } from '@googlemaps/google-maps-services-js';
import { normalizeScore } from '../utils/scoring';

const client = new Client({});
const GOOGLE_MAPS_API_KEY = process.env.GOOGLE_MAPS_API_KEY || '';

export interface PillarAResult {
  score: number;
  subScores: {
    a1: number; // Competitor Density
    a2: number; // Competitor Quality
    a3: number; // Market Saturation Index
    a4: number; // Competitive Advantage
  };
  rawData: any;
}

/**
 * Pillar A: Competition & Saturation Analysis
 */
export const analyzePillarA = async (
  latitude: number,
  longitude: number,
  radius: number,
  industry: string
): Promise<PillarAResult> => {
  try {
    // Map industry to Google Places types
    const placeTypes = getPlaceTypesForIndustry(industry);
    
    // A.1: Competitor Density - Count nearby competitors
    const nearbyPlaces = await client.placesNearby({
      params: {
        location: { lat: latitude, lng: longitude },
        radius,
        type: placeTypes[0],
        key: GOOGLE_MAPS_API_KEY,
      },
    });
    
    const competitorCount = nearbyPlaces.data.results?.length || 0;
    // Normalize: 0 competitors = 10, 20+ competitors = 0 (inverse)
    const subScoreA1 = normalizeScore(competitorCount, 0, 20, true);
    
    // A.2: Competitor Quality - Average rating of competitors
    const ratings = nearbyPlaces.data.results
      ?.filter(place => place.rating)
      .map(place => place.rating || 0) || [];
    const avgRating = ratings.length > 0 
      ? ratings.reduce((sum, r) => sum + r, 0) / ratings.length 
      : 3.0;
    // Normalize: Lower competitor quality is better (inverse)
    const subScoreA2 = normalizeScore(avgRating, 1, 5, true);
    
    // A.3: Market Saturation Index - Density per square km
    const areaKm2 = (Math.PI * radius * radius) / 1_000_000;
    const densityPerKm2 = competitorCount / areaKm2;
    // Normalize: 0 = 10, 100+ per km² = 0 (inverse)
    const subScoreA3 = normalizeScore(densityPerKm2, 0, 100, true);
    
    // A.4: Competitive Advantage - Simulated based on competitor strength
    // Higher score when competitors are weak or few
    const competitiveAdvantage = (subScoreA1 + subScoreA2) / 2;
    const subScoreA4 = competitiveAdvantage;
    
    // Calculate pillar score (average of sub-scores)
    const pillarScore = (subScoreA1 + subScoreA2 + subScoreA3 + subScoreA4) / 4;
    
    return {
      score: Math.round(pillarScore * 100) / 100,
      subScores: {
        a1: subScoreA1,
        a2: subScoreA2,
        a3: subScoreA3,
        a4: subScoreA4,
      },
      rawData: {
        competitorCount,
        avgRating,
        densityPerKm2,
        competitors: nearbyPlaces.data.results?.slice(0, 5).map(p => ({
          name: p.name,
          rating: p.rating,
          userRatingsTotal: p.user_ratings_total,
        })),
      },
    };
  } catch (error) {
    console.error('Pillar A analysis error:', error);
    // Return neutral scores on error
    return {
      score: 5.0,
      subScores: { a1: 5.0, a2: 5.0, a3: 5.0, a4: 5.0 },
      rawData: { error: 'Failed to fetch data' },
    };
  }
};

const getPlaceTypesForIndustry = (industry: string): string[] => {
  const industryMap: Record<string, string[]> = {
    'F&B': ['restaurant', 'cafe', 'food'],
    'Retail': ['store', 'shopping_mall', 'clothing_store'],
    'Service': ['beauty_salon', 'gym', 'spa'],
  };
  return industryMap[industry] || ['establishment'];
};
