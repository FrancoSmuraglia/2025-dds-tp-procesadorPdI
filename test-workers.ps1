# Script PowerShell para probar múltiples workers de procesamiento de PDIs
# Uso: .\test-workers.ps1 -NumPdis 10

param(
    [int]$NumPdis = 10,
    [string]$BaseUrl = "http://localhost:8083"
)

Write-Host "🚀 Iniciando prueba de workers con $NumPdis PDIs" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

function Create-Pdi {
    param(
        [int]$Index
    )
    
    $hechoId = "hecho-test-$Index"
    $momento = "2025-11-14T10:$('{0:D2}' -f $Index):00"
    
    Write-Host "📝 Creando PDI #$Index..." -ForegroundColor Yellow
    
    $body = @{
        hechoId = $hechoId
        descripcion = "PDI de prueba $Index para validar workers"
        lugar = "Test Location $Index"
        momento = $momento
        contenido = "Contenido del PDI número $Index"
        imagenUrl = "https://picsum.photos/seed/$Index/400/300"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/pdis" `
            -Method Post `
            -ContentType "application/json" `
            -Body $body
        
        Write-Host "✅ PDI #$Index creado con ID: $($response.id)" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Error creando PDI #$Index" -ForegroundColor Red
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
}

# Crear PDIs
Write-Host "🔄 Creando $NumPdis PDIs..." -ForegroundColor Cyan
Write-Host ""

for ($i = 1; $i -le $NumPdis; $i++) {
    Create-Pdi -Index $i
    Start-Sleep -Milliseconds 500  # Pausa entre requests
}

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "✅ Todos los PDIs creados" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Ahora revisa los logs de tus workers:" -ForegroundColor Yellow
Write-Host "   - Worker 1: Debería mostrar procesamiento de algunos PDIs"
Write-Host "   - Worker 2: Debería mostrar procesamiento de otros PDIs"
Write-Host ""
Write-Host "🔍 Para verificar procesamiento, consulta un PDI:" -ForegroundColor Yellow
Write-Host "   Invoke-RestMethod -Uri $BaseUrl/api/pdis/1" -ForegroundColor White
Write-Host ""
Write-Host "📈 Métricas en Datadog (si configurado):" -ForegroundColor Yellow
Write-Host "   dds.pdi.processed - Contador de PDIs procesados"
Write-Host "   dds.pdi.process.latency - Latencia de procesamiento"
Write-Host ""
