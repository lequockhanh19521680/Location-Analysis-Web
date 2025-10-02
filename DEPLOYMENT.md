# Deployment Guide - Location Score AI V4.0

## Quick Start Deployment

### Option 1: Docker Compose (Recommended)

1. **Prerequisites**
   - Docker 20.10+
   - Docker Compose 2.0+
   - Google Maps API Key

2. **Setup**
   ```bash
   # Clone repository
   git clone https://github.com/lequockhanh19521680/Location-Analysis-Web.git
   cd Location-Analysis-Web

   # Configure environment
   cp .env.example .env
   # Edit .env with your credentials
   nano .env

   # Start all services
   docker-compose up -d
   ```

3. **Access**
   - Frontend: http://localhost:3000
   - Auth Service: http://localhost:3001
   - Core Service: http://localhost:3002
   - PostgreSQL: localhost:5432

4. **Initialize Database**
   ```bash
   docker-compose exec postgres psql -U lsai_user -d lsai_db
   ```

### Option 2: Manual Deployment

#### 1. Database Setup
```bash
# Start PostgreSQL
docker run -d \
  --name lsai-postgres \
  -e POSTGRES_USER=lsai_user \
  -e POSTGRES_PASSWORD=lsai_password \
  -e POSTGRES_DB=lsai_db \
  -p 5432:5432 \
  postgres:15-alpine

# Run migrations
cd database
npm install
npx prisma migrate dev
npx prisma generate
```

#### 2. Auth Service
```bash
cd auth-service
npm install
npm run build
npm start
# Service runs on port 3001
```

#### 3. Core Service
```bash
cd core-service
npm install
npm run build
npm start
# Service runs on port 3002
```

#### 4. Frontend
```bash
cd frontend
npm install
npm run build
npm start
# Service runs on port 3000
```

## Production Deployment

### AWS Deployment

#### 1. RDS PostgreSQL Setup
```bash
# Create RDS instance
aws rds create-db-instance \
  --db-instance-identifier lsai-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username lsai_user \
  --master-user-password YOUR_PASSWORD \
  --allocated-storage 20
```

#### 2. ECS Setup
```bash
# Create ECR repositories
aws ecr create-repository --repository-name lsai-frontend
aws ecr create-repository --repository-name lsai-auth-service
aws ecr create-repository --repository-name lsai-core-service

# Build and push images
docker build -t lsai-frontend ./frontend
docker tag lsai-frontend:latest AWS_ACCOUNT.dkr.ecr.REGION.amazonaws.com/lsai-frontend:latest
docker push AWS_ACCOUNT.dkr.ecr.REGION.amazonaws.com/lsai-frontend:latest
```

#### 3. ECS Task Definitions
Create task definitions for each service with environment variables from Secrets Manager.

### Google Cloud Platform

#### 1. Cloud SQL PostgreSQL
```bash
gcloud sql instances create lsai-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1
```

#### 2. Cloud Run Deployment
```bash
# Build containers
gcloud builds submit --tag gcr.io/PROJECT_ID/lsai-frontend ./frontend
gcloud builds submit --tag gcr.io/PROJECT_ID/lsai-auth-service ./auth-service
gcloud builds submit --tag gcr.io/PROJECT_ID/lsai-core-service ./core-service

# Deploy services
gcloud run deploy lsai-frontend \
  --image gcr.io/PROJECT_ID/lsai-frontend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

### Kubernetes Deployment

#### 1. Create Namespace
```bash
kubectl create namespace lsai
```

#### 2. Deploy Database
```yaml
# postgres-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: lsai
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15-alpine
        env:
        - name: POSTGRES_USER
          value: "lsai_user"
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: lsai-secrets
              key: db-password
        - name: POSTGRES_DB
          value: "lsai_db"
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: postgres-storage
        persistentVolumeClaim:
          claimName: postgres-pvc
```

#### 3. Deploy Services
```bash
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/auth-service-deployment.yaml
kubectl apply -f k8s/core-service-deployment.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/ingress.yaml
```

## Environment Variables

### Required Variables

```env
# Database
DATABASE_URL=postgresql://user:password@host:5432/database

# JWT Secrets (Generate with: openssl rand -base64 32)
JWT_SECRET=your-32-char-secret-key
NEXTAUTH_SECRET=your-nextauth-secret-key

# Google Maps API
GOOGLE_MAPS_API_KEY=your-google-maps-api-key

# OAuth (Optional)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
FACEBOOK_APP_ID=your-facebook-app-id
FACEBOOK_APP_SECRET=your-facebook-app-secret

# Service URLs
NEXT_PUBLIC_AUTH_SERVICE_URL=https://auth.yourdomain.com
NEXT_PUBLIC_CORE_SERVICE_URL=https://api.yourdomain.com
NEXTAUTH_URL=https://yourdomain.com

# Payment (Optional)
STRIPE_SECRET_KEY=your-stripe-key
VNPAY_TMN_CODE=your-vnpay-code
VNPAY_SECRET_KEY=your-vnpay-secret
```

## SSL/TLS Setup

### Let's Encrypt with Nginx
```nginx
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /auth/ {
        proxy_pass http://localhost:3001;
    }

    location /core/ {
        proxy_pass http://localhost:3002;
    }
}
```

## Monitoring & Logging

### Prometheus + Grafana
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'lsai-services'
    static_configs:
      - targets:
        - 'localhost:3001'
        - 'localhost:3002'
        - 'localhost:3000'
```

### ELK Stack
```bash
docker run -d \
  --name elasticsearch \
  -p 9200:9200 \
  -e "discovery.type=single-node" \
  elasticsearch:8.11.0

docker run -d \
  --name kibana \
  -p 5601:5601 \
  --link elasticsearch:elasticsearch \
  kibana:8.11.0
```

## Backup & Recovery

### Database Backup
```bash
# Automated backup script
#!/bin/bash
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)

docker exec lsai-postgres pg_dump -U lsai_user lsai_db > \
  $BACKUP_DIR/lsai_db_$DATE.sql

# Keep only last 7 days
find $BACKUP_DIR -name "lsai_db_*.sql" -mtime +7 -delete
```

### Restore Database
```bash
docker exec -i lsai-postgres psql -U lsai_user lsai_db < backup.sql
```

## Scaling

### Horizontal Scaling
```bash
# Scale services in Docker Swarm
docker service scale lsai_frontend=3
docker service scale lsai_core-service=3

# Scale in Kubernetes
kubectl scale deployment frontend --replicas=3 -n lsai
kubectl scale deployment core-service --replicas=3 -n lsai
```

### Load Balancing
Use Nginx, HAProxy, or cloud load balancers to distribute traffic across service instances.

## Health Checks

### Docker Compose Health Checks
```yaml
services:
  auth-service:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3001/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

### Kubernetes Liveness & Readiness Probes
```yaml
livenessProbe:
  httpGet:
    path: /health
    port: 3001
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /health
    port: 3001
  initialDelaySeconds: 5
  periodSeconds: 5
```

## Security Checklist

- [ ] Use HTTPS/TLS for all services
- [ ] Store secrets in secure vault (AWS Secrets Manager, GCP Secret Manager)
- [ ] Enable CORS only for trusted domains
- [ ] Implement rate limiting
- [ ] Use security headers (Helmet.js)
- [ ] Regular security updates
- [ ] Database encryption at rest
- [ ] API key rotation
- [ ] Regular penetration testing

## Performance Optimization

1. **Caching**: Implement Redis for API response caching
2. **CDN**: Use CloudFlare or AWS CloudFront for static assets
3. **Database**: Add indexes, connection pooling
4. **API**: Implement request batching and debouncing
5. **Frontend**: Code splitting, lazy loading, image optimization

## Troubleshooting

### Service Won't Start
```bash
# Check logs
docker-compose logs -f service-name

# Check port conflicts
lsof -i :3000
lsof -i :3001
lsof -i :3002
```

### Database Connection Issues
```bash
# Test connection
docker exec -it lsai-postgres psql -U lsai_user -d lsai_db -c "SELECT 1;"

# Check DATABASE_URL format
echo $DATABASE_URL
```

### Google Maps API Errors
- Verify API key is valid
- Enable required APIs in Google Cloud Console
- Check billing is enabled
- Verify request quotas

## Support

For deployment issues:
- GitHub Issues: [Create Issue](https://github.com/lequockhanh19521680/Location-Analysis-Web/issues)
- Documentation: Check README.md and DEVELOPMENT.md
- Email: support@locationscore.ai
