import { Response } from 'express';
import { z } from 'zod';
import { AuthRequest } from '../middleware/auth.middleware';
import prisma from '../utils/prisma';
import { calculateWeightedScore, applyCriticalRiskThreshold } from '../utils/scoring';
import { analyzePillarA } from '../services/pillarA.service';
import { analyzePillarB } from '../services/pillarB.service';
import { analyzePillarC } from '../services/pillarC.service';
import { analyzePillarD } from '../services/pillarD.service';
import { analyzePillarE } from '../services/pillarE.service';

// Validation schema
const analyzeLocationSchema = z.object({
  address: z.string().optional(),
  latitude: z.number().min(-90).max(90),
  longitude: z.number().min(-180).max(180),
  industry: z.enum(['F&B', 'Retail', 'Service']),
  industrySubType: z.string().optional(),
  radius: z.number().min(100).max(5000).default(500),
  weights: z.object({
    weightA: z.number().min(0).max(1).default(0.30),
    weightB: z.number().min(0).max(1).default(0.25),
    weightC: z.number().min(0).max(1).default(0.30),
    weightD: z.number().min(0).max(1).default(0.10),
    weightE: z.number().min(0).max(1).default(0.05),
  }).optional(),
});

export const analyzeLocation = async (req: AuthRequest, res: Response) => {
  try {
    const startTime = Date.now();
    
    // Validate input
    const input = analyzeLocationSchema.parse(req.body);
    const { latitude, longitude, industry, industrySubType, radius, address } = input;
    
    // Set default weights if not provided
    const weights = input.weights || {
      weightA: 0.30,
      weightB: 0.25,
      weightC: 0.30,
      weightD: 0.10,
      weightE: 0.05,
    };
    
    // Validate weights sum to ~1.0
    const weightSum = Object.values(weights).reduce((sum, w) => sum + w, 0);
    if (Math.abs(weightSum - 1.0) > 0.01) {
      return res.status(400).json({ 
        error: 'Weights must sum to 1.0',
        currentSum: weightSum 
      });
    }
    
    console.log(`Starting analysis for location: ${latitude}, ${longitude}`);
    
    // Analyze all 5 pillars in parallel
    const [pillarA, pillarB, pillarC, pillarD, pillarE] = await Promise.all([
      analyzePillarA(latitude, longitude, radius, industry),
      analyzePillarB(latitude, longitude, radius),
      analyzePillarC(latitude, longitude, industry),
      analyzePillarD(latitude, longitude, radius),
      analyzePillarE(latitude, longitude),
    ]);
    
    // Calculate weighted total score
    const totalScore = calculateWeightedScore(
      pillarA.score,
      pillarB.score,
      pillarC.score,
      pillarD.score,
      pillarE.score,
      weights
    );
    
    // Apply NFR7 Critical Risk Threshold Logic
    const { finalScore, hasRiskAlert } = applyCriticalRiskThreshold(
      totalScore,
      pillarE.subScores.e1
    );
    
    // Save to database
    const analysisResult = await prisma.analysisResult.create({
      data: {
        userId: req.user!.id,
        address: address || `${latitude}, ${longitude}`,
        latitude,
        longitude,
        industry,
        industrySubType,
        radius,
        weightA: weights.weightA,
        weightB: weights.weightB,
        weightC: weights.weightC,
        weightD: weights.weightD,
        weightE: weights.weightE,
        
        // Pillar scores
        pillarA_score: pillarA.score,
        pillarB_score: pillarB.score,
        pillarC_score: pillarC.score,
        pillarD_score: pillarD.score,
        pillarE_score: pillarE.score,
        
        // Sub-scores A
        subScoreA1: pillarA.subScores.a1,
        subScoreA2: pillarA.subScores.a2,
        subScoreA3: pillarA.subScores.a3,
        subScoreA4: pillarA.subScores.a4,
        
        // Sub-scores B
        subScoreB1: pillarB.subScores.b1,
        subScoreB2: pillarB.subScores.b2,
        subScoreB3: pillarB.subScores.b3,
        subScoreB4: pillarB.subScores.b4,
        
        // Sub-scores C
        subScoreC1: pillarC.subScores.c1,
        subScoreC2: pillarC.subScores.c2,
        subScoreC3: pillarC.subScores.c3,
        
        // Sub-scores D
        subScoreD1: pillarD.subScores.d1,
        subScoreD2: pillarD.subScores.d2,
        subScoreD3: pillarD.subScores.d3,
        
        // Sub-scores E
        subScoreE1: pillarE.subScores.e1,
        subScoreE2: pillarE.subScores.e2,
        subScoreE3: pillarE.subScores.e3,
        
        totalScore: finalScore,
        hasRiskAlert,
        
        rawData: {
          pillarA: pillarA.rawData,
          pillarB: pillarB.rawData,
          pillarC: pillarC.rawData,
          pillarD: pillarD.rawData,
          pillarE: pillarE.rawData,
          analysisTime: Date.now() - startTime,
        },
      },
    });
    
    const responseTime = Date.now() - startTime;
    console.log(`Analysis completed in ${responseTime}ms`);
    
    res.json({
      id: analysisResult.id,
      location: {
        address: analysisResult.address,
        latitude,
        longitude,
        radius,
      },
      industry: {
        type: industry,
        subType: industrySubType,
      },
      scores: {
        total: finalScore,
        pillars: {
          A: {
            score: pillarA.score,
            name: 'Competition & Saturation',
            subScores: {
              'A.1': { score: pillarA.subScores.a1, name: 'Competitor Density' },
              'A.2': { score: pillarA.subScores.a2, name: 'Competitor Quality' },
              'A.3': { score: pillarA.subScores.a3, name: 'Market Saturation' },
              'A.4': { score: pillarA.subScores.a4, name: 'Competitive Advantage' },
            },
          },
          B: {
            score: pillarB.score,
            name: 'Traffic & Accessibility',
            subScores: {
              'B.1': { score: pillarB.subScores.b1, name: 'Foot Traffic' },
              'B.2': { score: pillarB.subScores.b2, name: 'Public Transport' },
              'B.3': { score: pillarB.subScores.b3, name: 'Vehicle Access' },
              'B.4': { score: pillarB.subScores.b4, name: 'Visibility' },
            },
          },
          C: {
            score: pillarC.score,
            name: 'Socio-Economic',
            subScores: {
              'C.1': { score: pillarC.subScores.c1, name: 'Customer Match' },
              'C.2': { score: pillarC.subScores.c2, name: 'Population Density' },
              'C.3': { score: pillarC.subScores.c3, name: 'Income Level' },
            },
          },
          D: {
            score: pillarD.score,
            name: 'Infrastructure & Environment',
            subScores: {
              'D.1': { score: pillarD.subScores.d1, name: 'Parking' },
              'D.2': { score: pillarD.subScores.d2, name: 'Safety' },
              'D.3': { score: pillarD.subScores.d3, name: 'Aesthetics' },
            },
          },
          E: {
            score: pillarE.score,
            name: 'Macro & Legal Risk',
            subScores: {
              'E.1': { score: pillarE.subScores.e1, name: 'Urban Planning Risk' },
              'E.2': { score: pillarE.subScores.e2, name: 'Future Development' },
              'E.3': { score: pillarE.subScores.e3, name: 'Economic Outlook' },
            },
          },
        },
        weights,
      },
      riskAlert: hasRiskAlert ? {
        level: 'CRITICAL',
        message: 'High urban planning risk detected. Total score capped at 5.0/10.',
        affectedScore: 'E.1',
        recommendations: pillarE.rawData.warnings || [],
      } : null,
      rawData: {
        pillarA: pillarA.rawData,
        pillarB: pillarB.rawData,
        pillarC: pillarC.rawData,
        pillarD: pillarD.rawData,
        pillarE: pillarE.rawData,
      },
      metadata: {
        analysisTime: responseTime,
        timestamp: new Date().toISOString(),
      },
    });
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({ error: error.errors });
    }
    console.error('Analysis error:', error);
    res.status(500).json({ error: 'Analysis failed' });
  }
};

export const getAnalysisHistory = async (req: AuthRequest, res: Response) => {
  try {
    const { page = 1, limit = 10 } = req.query;
    const skip = (Number(page) - 1) * Number(limit);
    
    const [results, total] = await Promise.all([
      prisma.analysisResult.findMany({
        where: { userId: req.user!.id },
        orderBy: { createdAt: 'desc' },
        skip,
        take: Number(limit),
        select: {
          id: true,
          address: true,
          latitude: true,
          longitude: true,
          industry: true,
          totalScore: true,
          hasRiskAlert: true,
          createdAt: true,
        },
      }),
      prisma.analysisResult.count({
        where: { userId: req.user!.id },
      }),
    ]);
    
    res.json({
      results,
      pagination: {
        page: Number(page),
        limit: Number(limit),
        total,
        pages: Math.ceil(total / Number(limit)),
      },
    });
  } catch (error) {
    console.error('Get history error:', error);
    res.status(500).json({ error: 'Failed to fetch history' });
  }
};

export const getAnalysisById = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    
    const result = await prisma.analysisResult.findFirst({
      where: {
        id,
        userId: req.user!.id,
      },
    });
    
    if (!result) {
      return res.status(404).json({ error: 'Analysis not found' });
    }
    
    res.json(result);
  } catch (error) {
    console.error('Get analysis error:', error);
    res.status(500).json({ error: 'Failed to fetch analysis' });
  }
};
