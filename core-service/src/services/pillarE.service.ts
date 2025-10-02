import { normalizeScore } from '../utils/scoring';

export interface PillarEResult {
  score: number;
  subScores: {
    e1: number; // Urban Planning Risk (CRITICAL)
    e2: number; // Future Development
    e3: number; // Economic Outlook
  };
  rawData: any;
}

/**
 * Pillar E: Macro & Legal Risk Analysis
 * Note: This uses simulated/static data. In production, integrate with:
 * - Government urban planning databases
 * - Legal/zoning APIs
 * - Economic forecasting services
 */
export const analyzePillarE = async (
  latitude: number,
  longitude: number
): Promise<PillarEResult> => {
  try {
    // E.1: Urban Planning Risk - CRITICAL THRESHOLD
    // Check for construction/redevelopment risks
    const planningRisk = simulateUrbanPlanningRisk(latitude, longitude);
    const subScoreE1 = normalizeScore(planningRisk, 0, 100, true); // Inverse: high risk = low score
    
    // E.2: Future Development - Positive development projects
    const futureDevelopment = simulateFutureDevelopment(latitude, longitude);
    const subScoreE2 = normalizeScore(futureDevelopment, 0, 100, false);
    
    // E.3: Economic Outlook - Regional economic growth
    const economicOutlook = simulateEconomicOutlook(latitude, longitude);
    const subScoreE3 = normalizeScore(economicOutlook, 0, 100, false);
    
    const pillarScore = (subScoreE1 + subScoreE2 + subScoreE3) / 3;
    
    return {
      score: Math.round(pillarScore * 100) / 100,
      subScores: {
        e1: subScoreE1,
        e2: subScoreE2,
        e3: subScoreE3,
      },
      rawData: {
        planningRisk,
        futureDevelopment,
        economicOutlook,
        riskLevel: subScoreE1 < 2.0 ? 'CRITICAL' : subScoreE1 < 5.0 ? 'HIGH' : 'ACCEPTABLE',
        warnings: subScoreE1 < 2.0 ? [
          'High urban planning risk detected',
          'Potential zoning changes or redevelopment',
          'Recommend legal consultation before proceeding',
        ] : [],
        note: 'Simulated data - replace with real legal/planning API in production',
      },
    };
  } catch (error) {
    console.error('Pillar E analysis error:', error);
    return {
      score: 5.0,
      subScores: { e1: 5.0, e2: 5.0, e3: 5.0 },
      rawData: { error: 'Failed to calculate' },
    };
  }
};

// Simulation functions (replace with real API calls)
const simulateUrbanPlanningRisk = (lat: number, lng: number): number => {
  // Simulate: some areas have high planning risk
  const riskFactor = (Math.abs(lat * lng * 100)) % 100;
  
  // 20% chance of high risk area
  if (riskFactor > 80) {
    return 80; // High risk (will result in score < 2.0)
  } else if (riskFactor > 60) {
    return 50; // Medium risk
  } else {
    return 20; // Low risk (good score)
  }
};

const simulateFutureDevelopment = (lat: number, lng: number): number => {
  // Simulate: positive development projects
  const devScore = (Math.abs(lat + lng) * 10) % 100;
  return Math.max(40, devScore); // Most areas have some positive outlook
};

const simulateEconomicOutlook = (lat: number, lng: number): number => {
  // Simulate: general economic conditions
  const economicScore = (Math.abs(lat - lng) * 15) % 100;
  return Math.max(50, economicScore); // Generally positive outlook
};
