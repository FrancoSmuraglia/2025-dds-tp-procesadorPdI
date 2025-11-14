# Script para levantar múltiples workers locales
# Uso: .\start-workers.ps1 -NumWorkers 2

param(
    [int]$NumWorkers = 2,
    [int]$StartPort = 8083
)

Write-Host "🚀 Levantando $NumWorkers workers locales..." -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que Maven está instalado
try {
    $null = mvn -version
}
catch {
    Write-Host "❌ Error: Maven no está instalado o no está en PATH" -ForegroundColor Red
    exit 1
}

# Función para iniciar un worker
function Start-Worker {
    param(
        [int]$WorkerNum,
        [int]$Port
    )
    
    Write-Host "🔧 Iniciando Worker $WorkerNum en puerto $Port..." -ForegroundColor Yellow
    
    # Crear un nuevo proceso de PowerShell para cada worker
    $command = @"
`$env:SERVER_PORT = $Port
Set-Location -Path '$PSScriptRoot'
Write-Host '🟢 Worker $WorkerNum iniciado en puerto $Port' -ForegroundColor Green
Write-Host '📡 Escuchando cola: pdi_cola_procesador' -ForegroundColor Cyan
Write-Host '================================================' -ForegroundColor Cyan
./mvnw spring-boot:run
"@
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
    
    Write-Host "✅ Worker $WorkerNum iniciado" -ForegroundColor Green
    Start-Sleep -Seconds 2
}

# Iniciar workers
for ($i = 1; $i -le $NumWorkers; $i++) {
    $port = $StartPort + ($i - 1)
    Start-Worker -WorkerNum $i -Port $port
}

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "✅ Todos los workers iniciados" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Workers activos:" -ForegroundColor Yellow
for ($i = 1; $i -le $NumWorkers; $i++) {
    $port = $StartPort + ($i - 1)
    Write-Host "   Worker $i → http://localhost:$port" -ForegroundColor White
}
Write-Host ""
Write-Host "🧪 Para probar la distribución de carga:" -ForegroundColor Yellow
Write-Host "   .\test-workers.ps1 -NumPdis 10" -ForegroundColor White
Write-Host ""
Write-Host "🛑 Para detener todos los workers:" -ForegroundColor Yellow
Write-Host "   Cierra las ventanas de PowerShell que se abrieron" -ForegroundColor White
Write-Host ""
