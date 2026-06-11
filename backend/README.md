# ATU Campus Backend

Local MVP backend for student verification.

## Run

```powershell
node backend\server.js
```

## Endpoints

- `GET /health`
- `GET /students?query=ali`
- `POST /verify-card`
- `POST /login`

The server reads `backend/data/students.json`.

Security note: this is a development backend. Passwords from the source Excel file must be hashed and moved into a real database before production.
