$ErrorActionPreference = "Stop"

$python = (Get-Command python -ErrorAction SilentlyContinue)
if (-not $python) {
  throw "Python tapılmadı. Python 3.10+ quraşdır və PATH-ə əlavə et."
}

$root = Split-Path -Parent $PSScriptRoot
$servicePath = Join-Path $root "backend\face_verification\service.py"

if (-not (Test-Path $servicePath)) {
  throw "Face verification service.py tapılmadı: $servicePath"
}

if (-not $env:PORT) {
  $env:PORT = "8090"
}

Write-Host "ATU Face Verification service starting on port $($env:PORT)..." -ForegroundColor Cyan
python $servicePath
