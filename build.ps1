param(
    [string]$ConnectorVersion = "9.7.0"
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$libDir = Join-Path $projectRoot "lib"
$outDir = Join-Path $projectRoot "out"
$jarName = "mysql-connector-j-$ConnectorVersion.jar"
$jarPath = Join-Path $libDir $jarName
$jarUrl = "https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/$ConnectorVersion/$jarName"

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    Write-Host "JDK not found. Install JDK 17+ and make sure javac is in PATH." -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir | Out-Null
}

if (-not (Test-Path $jarPath)) {
    Write-Host "MySQL Connector/J not found. Downloading version $ConnectorVersion..." -ForegroundColor Cyan
    try {
        Invoke-WebRequest -Uri $jarUrl -OutFile $jarPath
    } catch {
        Write-Host "Could not download MySQL Connector/J." -ForegroundColor Red
        Write-Host "Check the internet connection and try again." -ForegroundColor Red
        if (Test-Path $jarPath) {
            Remove-Item $jarPath -Force
        }
        exit 1
    }

    if ((Get-Item $jarPath).Length -lt 100000) {
        Remove-Item $jarPath -Force
        Write-Host "Downloaded Connector/J file looks invalid. Try again." -ForegroundColor Red
        exit 1
    }

    Write-Host "MySQL Connector/J downloaded to lib/$jarName" -ForegroundColor Green
}

if (Test-Path $outDir) {
    Remove-Item -Recurse -Force $outDir
}
New-Item -ItemType Directory -Path $outDir | Out-Null

$sources = Get-ChildItem -Path (Join-Path $projectRoot "src\vrms") -Filter "*.java" |
    ForEach-Object { $_.FullName }

Write-Host "Compiling VRMS..." -ForegroundColor Cyan
& javac -cp "$libDir\*" -d $outDir $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Compilation successful." -ForegroundColor Green
