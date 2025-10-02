import { Router } from 'express';
import { authMiddleware } from '../middleware/auth.middleware';
import { analyzeLocation, getAnalysisHistory, getAnalysisById } from '../controllers/score.controller';

const router = Router();

// All routes require authentication
router.use(authMiddleware);

router.post('/score', analyzeLocation);
router.get('/history', getAnalysisHistory);
router.get('/analysis/:id', getAnalysisById);

export default router;
