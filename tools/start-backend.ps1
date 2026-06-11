$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$BundledNode = "C:\Users\Admin\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe"
$SystemNodeCommand = Get-Command node -ErrorAction SilentlyContinue

if (Test-Path $BundledNode) {
    $Node = $BundledNode
} elseif ($SystemNodeCommand) {
    $Node = $SystemNodeCommand.Source
} else {
    throw "Node.js tapilmadi."
}

function Get-LanIp {
    $candidates = Get-NetIPAddress -AddressFamily IPv4 -ErrorAction SilentlyContinue |
        Where-Object {
            $_.IPAddress -notmatch '^127\.' -and
            $_.IPAddress -notmatch '^169\.254\.' -and
            $_.PrefixOrigin -ne 'WellKnown'
        } |
        Sort-Object InterfaceMetric

    return ($candidates | Select-Object -First 1 -ExpandProperty IPAddress)
}

function Ensure-FirewallRule {
    $ruleName = "ATU Campus Backend 8080"
    $existing = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue
    if (-not $existing) {
        try {
            New-NetFirewallRule `
                -DisplayName $ruleName `
                -Direction Inbound `
                -Action Allow `
                -Protocol TCP `
                -LocalPort 8080 `
                -Profile Private | Out-Null
            Write-Host "Firewall rule yaradildi: $ruleName" -ForegroundColor Green
        } catch {
            Write-Host "Firewall rule acilmadi. PowerShell-i Administrator kimi acib yeniden isledin." -ForegroundColor Yellow
        }
    } else {
        Write-Host "Firewall rule artiq movcuddur: $ruleName" -ForegroundColor DarkGreen
    }
}

Set-Location $ProjectRoot

if (-not $env:OPENAI_API_KEY) {
    Write-Host "WARNING: OPENAI_API_KEY set olunmayib. AI chat demo cavab verecek." -ForegroundColor Yellow
    Write-Host 'Real AI ucun evvelce: $env:OPENAI_API_KEY="SENIN_API_KEY"' -ForegroundColor Yellow
} else {
    Write-Host "OPENAI_API_KEY tapildi. AI chat aktiv rejimde baslayir." -ForegroundColor Green
}

Ensure-FirewallRule

$LanIp = Get-LanIp
Write-Host "Local backend: http://127.0.0.1:8080" -ForegroundColor Cyan
if ($LanIp) {
    Write-Host "Wi-Fi backend: http://$LanIp`:8080" -ForegroundColor Green
    Write-Host "Telefonda bu unvani Profil > Backend server bolmesinde yaz." -ForegroundColor Green
} else {
    Write-Host "LAN IP tapilmadi. Wi-Fi ile test ucun komputer ve telefon eyni sebekede olmalidir." -ForegroundColor Yellow
}

& $Node "backend\server.js"
