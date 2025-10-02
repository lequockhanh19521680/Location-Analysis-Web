/**
 * Normalizes a score to 0-10 scale
 * @param value - The value to normalize
 * @param minRange - Minimum value in the range
 * @param maxRange - Maximum value in the range
 * @param isInverse - If true, higher values result in lower scores
 */
export const normalizeScore = (
  value: number,
  minRange: number,
  maxRange: number,
  isInverse: boolean = false
): number => {
  // Clamp value to range
  const clampedValue = Math.max(minRange, Math.min(maxRange, value));
  
  // Linear normalization to 0-10
  let normalized = ((clampedValue - minRange) / (maxRange - minRange)) * 10;
  
  // Invert if needed
  if (isInverse) {
    normalized = 10 - normalized;
  }
  
  return Math.round(normalized * 100) / 100; // Round to 2 decimal places
};

/**
 * Calculates weighted sum of pillar scores
 */
export const calculateWeightedScore = (
  pillarA: number,
  pillarB: number,
  pillarC: number,
  pillarD: number,
  pillarE: number,
  weights: {
    weightA: number;
    weightB: number;
    weightC: number;
    weightD: number;
    weightE: number;
  }
): number => {
  const total =
    pillarA * weights.weightA +
    pillarB * weights.weightB +
    pillarC * weights.weightC +
    pillarD * weights.weightD +
    pillarE * weights.weightE;
  
  return Math.round(total * 100) / 100;
};

/**
 * NFR7 Logic: Applies critical risk threshold
 * If E.1 (Urban Planning Risk) < 2/10, cap total score at 5.0
 */
export const applyCriticalRiskThreshold = (
  totalScore: number,
  subScoreE1: number
): { finalScore: number; hasRiskAlert: boolean } => {
  const CRITICAL_THRESHOLD = 2.0;
  const MAX_SCORE_WITH_RISK = 5.0;
  
  if (subScoreE1 < CRITICAL_THRESHOLD) {
    return {
      finalScore: Math.min(totalScore, MAX_SCORE_WITH_RISK),
      hasRiskAlert: true,
    };
  }
  
  return {
    finalScore: totalScore,
    hasRiskAlert: false,
  };
};
