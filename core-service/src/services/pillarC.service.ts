import { normalizeScore } from '../utils/scoring';

export interface PillarCResult {
  score: number;
  subScores: {
    c1: number; // Target Customer Match
    c2: number; // Population Density
    c3: number; // Income Level
  };
  rawData: any;
}

/**
 * Pillar C: Socio-Economic Analysis
 * Note: This uses simulated data. In production, integrate with census/demographic APIs
 */
export const analyzePillarC = async (
  latitude: number,
  longitude: number,
  industry: string
): Promise<PillarCResult> => {
  try {
    // Simulated demographic data based on coordinates
    // In production: Use census API, government data, or third-party services
    
    // C.1: Target Customer Match - Demographic alignment
    const customerMatchScore = simulateCustomerMatch(latitude, longitude, industry);
    const subScoreC1 = normalizeScore(customerMatchScore, 0, 100, false);
    
    // C.2: Population Density - People per km²
    const populationDensity = simulatePopulationDensity(latitude, longitude);
    const subScoreC2 = normalizeScore(populationDensity, 0, 10000, false);
    
    // C.3: Income Level - Average household income
    const incomeLevel = simulateIncomeLevel(latitude, longitude);
    const subScoreC3 = normalizeScore(incomeLevel, 10000, 100000, false);
    
    const pillarScore = (subScoreC1 + subScoreC2 + subScoreC3) / 3;
    
    return {
      score: Math.round(pillarScore * 100) / 100,
      subScores: {
        c1: subScoreC1,
        c2: subScoreC2,
        c3: subScoreC3,
      },
      rawData: {
        customerMatchScore,
        populationDensity,
        incomeLevel,
        industryType: industry,
        note: 'Simulated data - replace with real demographic API in production',
      },
    };
  } catch (error) {
    console.error('Pillar C analysis error:', error);
    return {
      score: 5.0,
      subScores: { c1: 5.0, c2: 5.0, c3: 5.0 },
      rawData: { error: 'Failed to calculate' },
    };
  }
};

// Simulation functions (replace with real API calls)
const simulateCustomerMatch = (lat: number, lng: number, industry: string): number => {
  // Urban areas (higher coordinates) = better match for F&B/Retail
  const urbanScore = Math.abs(lat) + Math.abs(lng);
  const baseScore = (urbanScore % 100);
  
  const industryMultiplier: Record<string, number> = {
    'F&B': 1.2,
    'Retail': 1.1,
    'Service': 1.0,
  };
  
  return Math.min(100, baseScore * (industryMultiplier[industry] || 1.0));
};

const simulatePopulationDensity = (lat: number, lng: number): number => {
  // Simulate: urban areas have higher density
  const urbanFactor = (Math.abs(lat) + Math.abs(lng)) % 50;
  return 1000 + urbanFactor * 100;
};

const simulateIncomeLevel = (lat: number, lng: number): number => {
  // Simulate: varies by location
  const baseFactor = (Math.abs(lat * lng)) % 50;
  return 30000 + baseFactor * 1000;
};
