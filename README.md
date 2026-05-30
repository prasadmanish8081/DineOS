# 🍽️ DineOS — Smart QR Menu & Restaurant Management Platform

DineOS is a modern SaaS-based QR Menu and Restaurant Management System built for restaurants, cafés, and food businesses.

The platform allows customers to scan QR codes, browse digital menus, place orders instantly, and make secure online payments without waiting for staff.

It also provides powerful management tools for restaurant owners, kitchen staff, and admins with realtime order tracking and role-based access.

---

# 🚀 Features

## 📱 Customer Features

- Scan QR codes to access digital menus
- Browse food categories and menu items
- Veg / Non-Veg filtering
- Place orders directly from mobile
- Secure online payments using Razorpay
- Live order status tracking

---

## 👨‍🍳 Restaurant & Kitchen Features

- Restaurant management dashboard
- Menu & category management
- Table management with QR generation
- Realtime kitchen order updates using WebSockets
- Complete order lifecycle tracking

### Order Status Flow

- `PLACED`
- `ACCEPTED`
- `PREPARING`
- `READY`
- `SERVED`
- `COMPLETED`
- `CANCELLED`

---

# 🔐 Authentication & Security

- JWT-based authentication
- Role-based authorization
- BCrypt password encryption
- Secure Razorpay payment verification
- Protected APIs using Spring Security

---

# 🛠️ Tech Stack

## Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Maven
- WebSocket (STOMP)

## Frontend

- React.js
- Vite
- Tailwind CSS
- Axios
- React Router

## Payment Gateway

- Razorpay

---

# 📂 Project Structure

```bash
DineOS/
│
├── backend/      # Spring Boot Backend
├── frontend/     # React + Vite Frontend
├── docker/       # Docker Configurations
└── render.yaml   # Render Deployment Config
```

---

# ⚙️ Backend Setup

## Run Backend

```powershell
cd backend
.\mvnw.cmd -DskipTests spring-boot:run
```

OR

```powershell
.\backend\run-backend-dev.ps1
```

---

# 💻 Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on:

```bash
http://localhost:3000
```

---

# 🔑 Environment Variables

Configure the following environment variables before running the project:

```env
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

JWT_SECRET=
JWT_EXPIRATION_MS=
JWT_ISSUER=

MENU_BASE_URL=

RAZORPAY_KEY_ID=
RAZORPAY_KEY_SECRET=
RAZORPAY_WEBHOOK_SECRET=
RAZORPAY_CURRENCY=
```

---

# 📡 Main API Modules

## Authentication

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
```

## Restaurant Management

```http
POST /api/v1/restaurants
GET  /api/v1/restaurants/{id}
PUT  /api/v1/restaurants/{id}
```

## Menu Management

```http
POST   /api/v1/restaurants/{restaurantId}/menu-items
GET    /api/v1/restaurants/{restaurantId}/menu-items
PUT    /api/v1/restaurants/{restaurantId}/menu-items/{menuItemId}
DELETE /api/v1/restaurants/{restaurantId}/menu-items/{menuItemId}
```

## Table & QR Module

```http
POST /api/v1/restaurants/{restaurantId}/tables
GET  /api/v1/restaurants/{restaurantId}/tables/{tableId}/qr-code
```

## Orders

```http
POST  /api/v1/restaurants/{restaurantId}/tables/{tableId}/orders
PATCH /api/v1/restaurants/{restaurantId}/orders/{orderId}/status
GET   /api/v1/restaurants/{restaurantId}/orders
```

---

# ⚡ Realtime Kitchen Updates

DineOS uses WebSockets for realtime order broadcasting.

## WebSocket Endpoint

```bash
/ws
```

## Topics

```bash
/topic/restaurants/{restaurantId}/orders
/topic/restaurants/{restaurantId}/kitchen
```

## Events

- `NEW_ORDER`
- `ORDER_UPDATED`
- `ORDER_READY`

---

# 💳 Razorpay Payment Integration

Integrated Razorpay payment system with:

- Secure payment verification
- Webhook validation
- Payment failure handling
- Transaction history tracking

---

# 👥 User Roles

| Role | Access |
|------|--------|
| ADMIN | Full system access |
| OWNER | Restaurant management |
| KITCHEN | Kitchen operations |
| CUSTOMER | Menu browsing & ordering |

---

# 🌐 Deployment

The project is configured for deployment on Render.

## Backend
- Spring Boot Web Service

## Frontend
- React Static Site

### Frontend Environment Variable

```env
VITE_API_BASE_URL=your_backend_url
```

---

# 📌 Important Notes

- DTO-based API architecture
- Stateless JWT authentication
- Stable restaurant slug system
- Dynamic QR code generation
- Production-ready scalable architecture

---

# 🔗 Live Project

## 🌍 Frontend

https://dineos-1-yif5.onrender.com

## 💻 GitHub Repository

https://github.com/prasadmanish8081

---

# 📷 Future Improvements

- Multi-restaurant support
- Analytics dashboard
- AI-based food recommendations
- Inventory management
- Notification system
- Mobile application support

---

# 👨‍💻 Developer

**Manish Prasad**

- Java Backend Developer
- Spring Boot & React Developer
- MERN Stack Enthusiast

GitHub:  
https://github.com/prasadmanish8081/DineOS
