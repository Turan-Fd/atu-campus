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

## Student photos via Cloudinary

For hosted profile photos, keep `backend/data/student-photos.json` as a work-number to URL map.

1. Create a Cloudinary account
2. Set env vars in PowerShell:

```powershell
$env:CLOUDINARY_CLOUD_NAME="your-cloud-name"
$env:CLOUDINARY_API_KEY="your-api-key"
$env:CLOUDINARY_API_SECRET="your-api-secret"
```

3. Upload and rewrite the manifest:

```powershell
powershell -ExecutionPolicy Bypass -File tools\upload-student-photos-cloudinary.ps1
```

After that, deploy the updated `student-photos.json` and the backend will serve Cloudinary URLs directly.
