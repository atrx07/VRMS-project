param(
    [switch]$ResetDbConfig
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildScript = Join-Path $projectRoot "build.ps1"
$configPath = Join-Path $projectRoot ".vrms-db.json"
$libDir = Join-Path $projectRoot "lib"
$outDir = Join-Path $projectRoot "out"

function Convert-SecureStringToPlainText {
    param([Security.SecureString]$SecureString)

    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($SecureString)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    }
}

function Save-LocalDbConfig {
    param(
        [string]$User,
        [Security.SecureString]$Password
    )

    $config = [ordered]@{
        user = $User
        password = ConvertFrom-SecureString $Password
    }

    $config | ConvertTo-Json | Set-Content -Path $configPath -Encoding UTF8
    Write-Host "Saved MySQL login locally for this Windows user." -ForegroundColor DarkGray
}

function Read-LocalDbConfig {
    if (-not (Test-Path $configPath)) {
        return $null
    }

    try {
        $config = Get-Content -Path $configPath -Raw | ConvertFrom-Json
        $securePassword = ConvertTo-SecureString $config.password

        return @{
            User = [string]$config.user
            Password = Convert-SecureStringToPlainText $securePassword
        }
    } catch {
        Write-Host "Saved MySQL login could not be read. It will be recreated." -ForegroundColor Yellow
        Remove-Item $configPath -Force -ErrorAction SilentlyContinue
        return $null
    }
}

function Test-LocalMySqlPort {
    $client = New-Object System.Net.Sockets.TcpClient

    try {
        $task = $client.ConnectAsync("127.0.0.1", 3306)

        if (-not $task.Wait(1500)) {
            return $false
        }

        return $client.Connected
    } catch {
        return $false
    } finally {
        $client.Close()
    }
}

if ($ResetDbConfig -and (Test-Path $configPath)) {
    Remove-Item $configPath -Force
    Write-Host "Saved MySQL login cleared." -ForegroundColor Yellow
}

$dbUser = $env:VRMS_DB_USER
$dbPassword = $env:VRMS_DB_PASSWORD

if ([string]::IsNullOrWhiteSpace($dbUser)) {
    $dbUser = "root"
}

if ($null -eq $dbPassword) {
    $savedConfig = Read-LocalDbConfig

    if ($null -ne $savedConfig) {
        $dbUser = $savedConfig.User
        $dbPassword = $savedConfig.Password
        Write-Host "Using saved MySQL login for user '$dbUser'." -ForegroundColor DarkGray
    } else {
        Write-Host ""
        Write-Host "First-time MySQL setup for VRMS" -ForegroundColor Cyan

        $enteredUser = Read-Host "MySQL user [root]"
        if (-not [string]::IsNullOrWhiteSpace($enteredUser)) {
            $dbUser = $enteredUser.Trim()
        }

        $securePassword = Read-Host "MySQL password (input hidden; press Enter if none)" -AsSecureString
        $dbPassword = Convert-SecureStringToPlainText $securePassword

        Save-LocalDbConfig -User $dbUser -Password $securePassword
        Write-Host ""
    }
}

$env:VRMS_DB_USER = $dbUser
$env:VRMS_DB_PASSWORD = $dbPassword

if (-not $env:VRMS_DB_SERVER_URL -and -not $env:VRMS_DB_URL) {
    $mysqlServices = @(Get-Service -ErrorAction SilentlyContinue | Where-Object {
        $_.Name -like "MySQL*" -or $_.DisplayName -like "MySQL*"
    })

    foreach ($service in $mysqlServices) {
        if ($service.Status -ne "Running") {
            Write-Host "MySQL service '$($service.Name)' is stopped. Trying to start it..." -ForegroundColor Yellow
            try {
                Start-Service -Name $service.Name -ErrorAction Stop
                $service.WaitForStatus("Running", [TimeSpan]::FromSeconds(5))
            } catch {
                Write-Host "Could not start MySQL automatically. Start the MySQL service and run VRMS again." -ForegroundColor Red
                exit 1
            }
        }
    }

    if (-not (Test-LocalMySqlPort)) {
        Write-Host "MySQL is not reachable on localhost:3306." -ForegroundColor Red
        Write-Host "Install/start MySQL Server, then run VRMS again." -ForegroundColor Red
        exit 1
    }
}

& $buildScript
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host "Starting VRMS..." -ForegroundColor Cyan
& java -cp "$outDir;$libDir\*" vrms.Main
exit $LASTEXITCODE
