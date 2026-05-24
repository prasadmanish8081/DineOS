# DineOS Backend

Spring Boot 3 / Java 21 backend starter for a SaaS restaurant ordering platform.

## Backend

Run the backend with the bundled Maven wrapper from the repository root:

```powershell
.\mvnw.cmd -DskipTests spring-boot:run
```

Or use the helper script on Windows:

```powershell
.\run-backend-dev.ps1
```

Or run the one-command alias:

```powershell
.\start-backend.ps1
```

## Frontend

A production-ready React + Vite frontend starter lives in [`frontend/`](frontend/).
It includes:

- React Router routing
- Axios API services
- JWT auth persistence
- Tailwind CSS styling
- dashboard, auth, and public QR menu layouts

Run it with:

```bash
cd frontend
npm install
npm run dev
```

The frontend dev server runs on `http://localhost:3000`.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Maven

## Configuration

Set these environment variables before running:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `JWT_ISSUER`
- `MENU_BASE_URL`
- `RAZORPAY_KEY_ID`
- `RAZORPAY_KEY_SECRET`
- `RAZORPAY_WEBHOOK_SECRET`
- `RAZORPAY_CURRENCY`

Defaults are provided in `src/main/resources/application.yml` for local development.

If customers scan a QR code from a phone on the same Wi-Fi network, the backend will automatically detect a LAN-accessible frontend URL when `MENU_BASE_URL` is set to localhost. For a reliable phone scan experience, set `MENU_BASE_URL` to your PC's local IP and re-generate the QR code if needed.

## Main Endpoints

### Public

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Authenticated

- `GET /api/v1/users/me`
- `GET /api/v1/access/customer`
- `GET /api/v1/access/kitchen`
- `GET /api/v1/access/owner`
- `GET /api/v1/access/admin`

### Restaurant Module

- `POST /api/v1/restaurants`
- `PUT /api/v1/restaurants/{id}`
- `GET /api/v1/restaurants/{id}`
- `GET /api/v1/restaurants/slug/{slug}`

### Menu Module

- `POST /api/v1/restaurants/{restaurantId}/categories`
- `GET /api/v1/restaurants/{restaurantId}/categories`
- `POST /api/v1/restaurants/{restaurantId}/menu-items`
- `PUT /api/v1/restaurants/{restaurantId}/menu-items/{menuItemId}`
- `DELETE /api/v1/restaurants/{restaurantId}/menu-items/{menuItemId}`
- `GET /api/v1/restaurants/{restaurantId}/menu-items?veg=true|false`

### Table Module

- `POST /api/v1/restaurants/{restaurantId}/tables`
- `GET /api/v1/restaurants/{restaurantId}/tables/{tableId}`
- `GET /api/v1/restaurants/{restaurantId}/tables/{tableId}/qr-code`

### Ordering Module

- `POST /api/v1/restaurants/{restaurantId}/tables/{tableId}/orders`
- `PATCH /api/v1/restaurants/{restaurantId}/orders/{orderId}/status`
- `GET /api/v1/restaurants/{restaurantId}/orders`
- `GET /api/v1/restaurants/{restaurantId}/orders/pending`

Order statuses:
- `PLACED`
- `ACCEPTED`
- `PREPARING`
- `READY`
- `SERVED`
- `COMPLETED`
- `CANCELLED`

### Realtime Kitchen

- WebSocket endpoint: `/ws`
- Application prefix: `/app`
- Broadcast topics:
  - `/topic/restaurants/{restaurantId}/orders`
  - `/topic/restaurants/{restaurantId}/kitchen`

Supported realtime events:
- `NEW_ORDER`
- `ORDER_UPDATED`
- `ORDER_READY`

WebSocket clients should send the JWT in the STOMP `Authorization` header as `Bearer <token>` during `CONNECT`.

Architecture notes:
- Orders publish application events after the database transaction commits.
- A dedicated realtime broadcaster converts those events into WebSocket messages.
- This keeps the order service decoupled from socket transport and makes it easier to replace the broker later with a distributed messaging backend if needed.

### Payments

- `POST /api/v1/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/create-order`
- `POST /api/v1/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/verify`
- `POST /api/v1/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/failed`
- `POST /api/v1/payments/webhooks/razorpay`

Payment notes:
- Successful checkout must be verified on the server using the Razorpay payment signature.
- Webhooks are validated with `X-Razorpay-Signature` and deduplicated by `x-razorpay-event-id`.
- Payment details are stored in `payment_transactions` and webhook audit rows in `payment_webhook_events`.

## Role Model

- `ADMIN`
- `OWNER`
- `KITCHEN`
- `CUSTOMER`

Public registration creates `CUSTOMER` users by default.

## Notes

- Passwords are hashed with BCrypt.
- JWT tokens are stateless and validated on every request.
- Entities are not exposed directly; DTOs are used everywhere at the API boundary.
- For a real production rollout, add Flyway or Liquibase migrations before switching `ddl-auto` away from `update`.
- Restaurant slugs are kept stable so table QR links do not break after restaurant updates.
- QR codes encode the frontend menu URL from `MENU_BASE_URL` and fall back to `http://localhost:3000` in local development.

## Deployment (Render)

This repository uses Render to host both backend and frontend. A multi-service `render.yaml` config is included at the repository root.

Services defined in `render.yaml`:

- **DineOS Backend** (type: `web`) — builds with `./mvnw -DskipTests package` and runs the Spring Boot JAR.
- **DineOS Frontend** (type: `static`) — builds the React app from `frontend/` and publishes `frontend/dist`.

Render build commands are configured in `render.yaml`. After connecting your GitHub repo to Render, ensure these environment variables are set for the backend:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `JWT_ISSUER`
- `MENU_BASE_URL` (set this to the Render frontend URL once the frontend is deployed)
- `RAZORPAY_KEY_ID`
- `RAZORPAY_KEY_SECRET`
- `RAZORPAY_WEBHOOK_SECRET`
- `RAZORPAY_CURRENCY`

Frontend configuration:

- The frontend build runs `npm install && npm run build` in the `frontend/` folder and publishes `frontend/dist`.
- Set the frontend environment variable on Render (Static Site) or in the backend `MENU_BASE_URL`:
  - `VITE_API_BASE_URL` = your Render backend URL

After updating env vars in Render, trigger a redeploy from the Render dashboard or push a commit to the `main` branch.

If you previously created a Vercel project for this repo, remove it from the Vercel dashboard to avoid duplicate deployments.

### Optional: Deploy using Docker images on Render

If Render suggests using a Docker image, you can either continue with repo-based builds or provide Dockerfiles. I added two example Dockerfiles at `docker/backend/Dockerfile` and `docker/frontend/Dockerfile`.

Build and test locally:

```bash
# Backend (from repo root)
docker build -f docker/backend/Dockerfile -t dineos-backend:local .
docker run -e PORT=8080 -p 8080:8080 dineos-backend:local

# Frontend
docker build -f docker/frontend/Dockerfile -t dineos-frontend:local .
docker run -p 8080:80 dineos-frontend:local
```

Using Docker on Render:

- When creating the service, choose "Docker" and point Render to the appropriate Dockerfile path (e.g. `docker/backend/Dockerfile`).
- Render will build the image and deploy the container. Ensure you set the same environment variables listed above.

Notes:
- The backend Dockerfile is multi-stage (build with Maven, run with Eclipse Temurin JRE). It respects the `PORT` env var.
- The frontend Dockerfile builds the Vite app and serves it with Nginx (SPA routing via `nginx.conf`).

Choose whichever flow you're comfortable with — repo build (no Docker) or Docker images. If you want, I can remove the Dockerfiles afterward or adjust them for a specific registry. Thank you!
