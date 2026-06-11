$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$SdkAdb = Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe"
$ApkPath = Join-Path $ProjectRoot "app\build\outputs\apk\debug\app-debug.apk"

if (!(Test-Path $SdkAdb)) {
    throw "adb.exe tapılmadı. Android SDK platform-tools quraşdırılmalıdır."
}

Set-Location $ProjectRoot

Write-Host "Building latest ATU Campus APK..."
& ".\gradlew.bat" ":app:assembleDebug" "--no-daemon"

Write-Host ""
Write-Host "Connected devices:"
$DeviceLines = & $SdkAdb devices
$DeviceLines

$ConnectedSerials = $DeviceLines |
    Select-Object -Skip 1 |
    ForEach-Object {
        if ($_ -match "^(\S+)\s+device$") { $Matches[1] }
    }

$DeviceSerial = $ConnectedSerials |
    Where-Object { $_ -notlike "emulator-*" } |
    Select-Object -First 1

if (-not $DeviceSerial) {
    $DeviceSerial = $ConnectedSerials | Select-Object -First 1
}

if (-not $DeviceSerial) {
    throw "Aktiv Android cihaz tapılmadı. USB debugging icazəsini yoxlayın."
}

Write-Host "Selected device: $DeviceSerial" -ForegroundColor Green

Write-Host ""
Write-Host "Preparing local backend tunnel..."
& $SdkAdb -s $DeviceSerial reverse tcp:8080 tcp:8080
Write-Host "Active reverse tunnels:"
& $SdkAdb -s $DeviceSerial reverse --list

Write-Host ""
Write-Host "Checking backend health..."
try {
    $health = Invoke-RestMethod -Uri "http://127.0.0.1:8080/health" -TimeoutSec 3
    Write-Host ("Backend OK. Students: " + $health.students)
    $aiTest = Invoke-RestMethod -Uri "http://127.0.0.1:8080/ai-chat" -Method Post -ContentType "application/json" -Body '{"message":"Salam"}' -TimeoutSec 8
    if ($aiTest.answer) {
        Write-Host "AI endpoint OK."
    }
} catch {
    Write-Host "WARNING: Backend işləmirmiş kimi görünür. AI və verification işləməyə bilər." -ForegroundColor Yellow
    Write-Host "Ayrı terminalda aç: powershell -ExecutionPolicy Bypass -File tools\start-backend.ps1" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Installing update to phone..."
& $SdkAdb -s $DeviceSerial install -r -d $ApkPath

Write-Host ""
Write-Host "Installed package version:"
& $SdkAdb -s $DeviceSerial shell dumpsys package com.atu.campus | Select-String "versionCode|versionName" | Select-Object -First 4

Write-Host ""
Write-Host "Restarting ATU Campus..."
& $SdkAdb -s $DeviceSerial shell am force-stop com.atu.campus | Out-Null

Write-Host ""
Write-Host "Opening ATU Campus..."
& $SdkAdb -s $DeviceSerial shell monkey -p com.atu.campus 1 | Out-Null

Write-Host "Done."
