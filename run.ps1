$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildScript = Join-Path $projectRoot "build.ps1"

& $buildScript
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

$libDir = Join-Path $projectRoot "lib"
$outDir = Join-Path $projectRoot "out"

Write-Host "Starting VRMS..." -ForegroundColor Cyan
& java -cp "$outDir;$libDir\*" vrms.Main
exit $LASTEXITCODE
