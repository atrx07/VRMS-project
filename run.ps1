param(
    [switch]$ResetDbConfig,
    [switch]$Elevated
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildScript = Join-Path $projectRoot "build.ps1"
$configPath = Join-Path $projectRoot ".vrms-db.json"
$libDir = Join-Path $projectRoot "lib"
$outDir = Join-Path $projectRoot "out"
$startedMySqlServices = @()

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
            Source = "saved"
        }
    } catch {
        Write-Host "Saved MySQL login could not be read. It will be recreated." -ForegroundColor Yellow
        Remove-Item $configPath -Force -ErrorAction SilentlyContinue
        return $null
    }
}

function Prompt-DbCredentials {
    param([string]$DefaultUser = "root")

    Write-Host ""
    Write-Host "Enter MySQL login for VRMS" -ForegroundColor Cyan

    $enteredUser = Read-Host "MySQL user [$DefaultUser]"
    if ([string]::IsNullOrWhiteSpace($enteredUser)) {
        $enteredUser = $DefaultUser
    } else {
        $enteredUser = $enteredUser.Trim()
    }

    $securePassword = Read-Host "MySQL password (input hidden; press Enter if none)" -AsSecureString

    return @{
        User = $enteredUser
        Password = Convert-SecureStringToPlainText $securePassword
        SecurePassword = $securePassword
        Source = "prompt"
    }
}

function Test-IsAdministrator {
    $identity = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($identity)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Get-LocalMySqlServices {
    return @(Get-Service -ErrorAction SilentlyContinue | Where-Object {
        $_.Name -like "MySQL*" -or $_.DisplayName -like "MySQL*"
    })
}

function Restart-LauncherAsAdministratorIfNeeded {
    if ($Elevated -or (Test-IsAdministrator)) {
        return
    }

    $stoppedServices = @(Get-LocalMySqlServices | Where-Object { $_.Status -ne "Running" })
    if ($stoppedServices.Count -eq 0) {
        return
    }

    Write-Host "MySQL is stopped and Windows requires administrator permission to start it." -ForegroundColor Yellow
    Write-Host "Requesting permission now..." -ForegroundColor Cyan

    $arguments = "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`" -Elevated"
    if ($ResetDbConfig) {
        $arguments += " -ResetDbConfig"
    }

    try {
        $process = Start-Process -FilePath "powershell.exe" `
            -ArgumentList $arguments `
            -Verb RunAs `
            -Wait `
            -PassThru

        exit $process.ExitCode
    } catch {
        Write-Host "Administrator permission was not granted. VRMS cannot start MySQL." -ForegroundColor Red
        exit 1
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

function Start-LocalMySqlIfNeeded {
    $services = Get-LocalMySqlServices

    if ($services.Count -eq 0) {
        return
    }

    foreach ($service in $services) {
        if ($service.Status -eq "Running") {
            Write-Host "MySQL service '$($service.Name)' is already running." -ForegroundColor DarkGray
            continue
        }

        Write-Host "MySQL service '$($service.Name)' is stopped. Starting it for VRMS..." -ForegroundColor Yellow

        try {
            Start-Service -Name $service.Name -ErrorAction Stop
            $service.WaitForStatus("Running", [TimeSpan]::FromSeconds(8))
            $script:startedMySqlServices += $service.Name
            Write-Host "Started MySQL service '$($service.Name)'." -ForegroundColor Green
        } catch {
            Write-Host "Could not start MySQL automatically." -ForegroundColor Red
            Write-Host "Start the MySQL service manually or run this launcher with permission to manage services." -ForegroundColor Red
            throw
        }
    }
}

function Stop-MySqlServicesStartedByVrms {
    foreach ($serviceName in $script:startedMySqlServices) {
        try {
            $service = Get-Service -Name $serviceName -ErrorAction SilentlyContinue

            if ($null -ne $service -and $service.Status -eq "Running") {
                Write-Host "Stopping MySQL service '$serviceName' started by VRMS..." -ForegroundColor DarkGray
                Stop-Service -Name $serviceName -ErrorAction Stop
                $service.WaitForStatus("Stopped", [TimeSpan]::FromSeconds(8))
            }
        } catch {
            Write-Host "Could not stop MySQL service '$serviceName'. It may remain running." -ForegroundColor Yellow
        }
    }
}

function Test-DatabaseLogin {
    Write-Host "Checking MySQL login..." -ForegroundColor Cyan

    $output = & java -cp "$outDir;$libDir\*" vrms.DatabaseCheck 2>&1
    $code = $LASTEXITCODE

    if ($code -eq 0) {
        Write-Host "MySQL login verified." -ForegroundColor Green
        return $true
    }

    Write-Host ""
    Write-Host "MySQL login failed." -ForegroundColor Red

    foreach ($line in $output) {
        if (-not [string]::IsNullOrWhiteSpace([string]$line)) {
            Write-Host ([string]$line) -ForegroundColor Yellow
        }
    }

    return $false
}

Restart-LauncherAsAdministratorIfNeeded

if ($ResetDbConfig -and (Test-Path $configPath)) {
    Remove-Item $configPath -Force
    Write-Host "Saved MySQL login cleared." -ForegroundColor Yellow
}

$exitCode = 0

try {
    if (-not $env:VRMS_DB_SERVER_URL -and -not $env:VRMS_DB_URL) {
        Start-LocalMySqlIfNeeded

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

    $credentials = $null

    if ($null -ne $env:VRMS_DB_PASSWORD) {
        $user = $env:VRMS_DB_USER
        if ([string]::IsNullOrWhiteSpace($user)) {
            $user = "root"
        }

        $credentials = @{
            User = $user
            Password = $env:VRMS_DB_PASSWORD
            Source = "environment"
        }
    } else {
        $credentials = Read-LocalDbConfig
    }

    if ($null -eq $credentials) {
        $credentials = Prompt-DbCredentials -DefaultUser "root"
    }

    while ($true) {
        $env:VRMS_DB_USER = $credentials.User
        $env:VRMS_DB_PASSWORD = $credentials.Password

        if (Test-DatabaseLogin) {
            if ($credentials.Source -eq "prompt") {
                Save-LocalDbConfig -User $credentials.User -Password $credentials.SecurePassword
            }

            break
        }

        if ($credentials.Source -eq "saved" -and (Test-Path $configPath)) {
            Remove-Item $configPath -Force -ErrorAction SilentlyContinue
            Write-Host "Removed the invalid saved MySQL login." -ForegroundColor DarkGray
        }

        Write-Host ""
        $choice = Read-Host "Press Enter to re-enter MySQL login, or type Q to quit"
        if ($choice -match "^[Qq]$") {
            exit 1
        }

        $credentials = Prompt-DbCredentials -DefaultUser $credentials.User
    }

    Write-Host ""
    Write-Host "Starting VRMS..." -ForegroundColor Cyan
    & java -cp "$outDir;$libDir\*" vrms.Main
    $exitCode = $LASTEXITCODE
} finally {
    Stop-MySqlServicesStartedByVrms
}

exit $exitCode
