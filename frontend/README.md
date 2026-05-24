# DineOS Frontend

Vite + React + Tailwind starter for the DineOS restaurant SaaS platform.

## Scripts

- `npm install`
- `npm run dev`
- `npm run build`
- `npm run preview`

## Environment

Create a `.env` file from `.env.example`:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

## Notes

- JWT is persisted in `localStorage`.
- Axios automatically attaches the bearer token to API requests.
- Auth, restaurant, menu, table, order, payment, and analytics service modules are already scaffolded.
- Routes are wired for both the authenticated dashboard and the public QR menu flow.
