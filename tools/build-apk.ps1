param(
    [string]$OutputName = "ATU-Campus-debug.apk"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$ApkSource = Join-Path $ProjectRoot "app\build\outputs\apk\debug\app-debug.apk"
$DistDir = Join-Path $ProjectRoot "dist"
$ApkTarget = Join-Path $DistDir $OutputName

Set-Location $ProjectRoot

Write-Host "Building ATU Campus debug APK..."
& ".\gradlew.bat" ":app:assembleDebug" "--no-daemon"

if (!(Test-Path $DistDir)) {
    New-Item -ItemType Directory -Path $DistDir | Out-Null
}

Copy-Item -LiteralPath $ApkSource -Destination $ApkTarget -Force

Write-Host ""
Write-Host "APK ready:"
Write-Host $ApkTarget
