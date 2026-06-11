$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$BundledNode = "C:\Users\Admin\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe"
$SystemNodeCommand = Get-Command node -ErrorAction SilentlyContinue

if (Test-Path $BundledNode) {
    $Node = $BundledNode
} elseif ($SystemNodeCommand) {
    $Node = $SystemNodeCommand.Source
} else {
    throw "Node.js tapılmadı."
}

Write-Host "Stopping process on port 8080 if exists..."
$portLines = netstat -ano | Select-String ":8080\s+.*LISTENING"
foreach ($line in $portLines) {
    $parts = ($line.ToString() -split "\s+") | Where-Object { $_ }
    $pidValue = $parts[-1]
    if ($pidValue -match "^\d+$") {
        Write-Host "Killing PID $pidValue"
        Stop-Process -Id ([int]$pidValue) -Force -ErrorAction SilentlyContinue
    }
}

Set-Location $ProjectRoot

if (-not $env:OPENAI_API_KEY) {
    Write-Host "WARNING: OPENAI_API_KEY set olunmayıb. AI chat real cavab verməyəcək." -ForegroundColor Yellow
    Write-Host 'Real AI üçün əvvəlcə: $env:OPENAI_API_KEY="SENIN_API_KEY"' -ForegroundColor Yellow
} else {
    Write-Host "OPENAI_API_KEY tapıldı. AI chat aktiv rejimdə başlayır." -ForegroundColor Green
}

Write-Host "Backend restarted: http://127.0.0.1:8080"
& $Node "backend\server.js"
