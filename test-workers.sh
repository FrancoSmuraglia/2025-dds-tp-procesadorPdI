#!/bin/bash

# Script para probar múltiples workers de procesamiento de PDIs
# Uso: ./test-workers.sh [num_pdis]

NUM_PDIS=${1:-10}
BASE_URL="http://localhost:8083"

echo "🚀 Iniciando prueba de workers con $NUM_PDIS PDIs"
echo "=================================================="
echo ""

# Función para crear un PDI
create_pdi() {
    local index=$1
    local hecho_id="hecho-test-$index"
    
    echo "📝 Creando PDI #$index..."
    
    response=$(curl -s -X POST "$BASE_URL/api/pdis" \
        -H "Content-Type: application/json" \
        -d "{
            \"hechoId\": \"$hecho_id\",
            \"descripcion\": \"PDI de prueba $index para validar workers\",
            \"lugar\": \"Test Location $index\",
            \"momento\": \"2025-11-14T10:$(printf '%02d' $index):00\",
            \"contenido\": \"Contenido del PDI número $index\",
            \"imagenUrl\": \"https://picsum.photos/seed/$index/400/300\"
        }")
    
    pdi_id=$(echo $response | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    
    if [ -n "$pdi_id" ]; then
        echo "✅ PDI #$index creado con ID: $pdi_id"
    else
        echo "❌ Error creando PDI #$index"
        echo "   Respuesta: $response"
    fi
    
    echo ""
}

# Crear PDIs
echo "🔄 Creando $NUM_PDIS PDIs..."
echo ""

for i in $(seq 1 $NUM_PDIS); do
    create_pdi $i
    sleep 0.5  # Pequeña pausa entre requests
done

echo "=================================================="
echo "✅ Todos los PDIs creados"
echo ""
echo "📊 Ahora revisa los logs de tus workers:"
echo "   - Worker 1: Debería mostrar procesamiento de algunos PDIs"
echo "   - Worker 2: Debería mostrar procesamiento de otros PDIs"
echo ""
echo "🔍 Para verificar procesamiento, consulta un PDI:"
echo "   curl $BASE_URL/api/pdis/1"
echo ""
echo "📈 Métricas en Datadog (si configurado):"
echo "   dds.pdi.processed - Contador de PDIs procesados"
echo "   dds.pdi.process.latency - Latencia de procesamiento"
echo ""
