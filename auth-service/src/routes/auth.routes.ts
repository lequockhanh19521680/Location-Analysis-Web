import { Router } from 'express';
import { register, login, oauthLogin, verifyTokenEndpoint } from '../controllers/auth.controller';

const router = Router();

router.post('/register', register);
router.post('/login', login);
router.post('/oauth', oauthLogin);
router.get('/verify', verifyTokenEndpoint);

export default router;
