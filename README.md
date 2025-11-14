# 2025 - DDS - TP - Franco Smuraglia - ProcesadorPdI

## 📋 Descripción

Módulo encargado del **procesamiento asíncrono de PDIs** (Piezas de Información) mediante workers distribuidos. Incluye:
- **OCR** (extracción de texto de imágenes)
- **Etiquetado automático** con IA (ApiLayer Image Labeling)
- **Sistema de workers** escalable con RabbitMQ

## 🏗️ Arquitectura de Workers

Este módulo implementa un sistema de **workers distribuidos** que permite:
- ✅ Procesamiento asíncrono (no bloquea el endpoint POST)
- ✅ Escalabilidad horizontal (agregar más workers sin cambios de código)
- ✅ Garantía de procesamiento único (cada PDI se procesa UNA VEZ)
- ✅ Distribución automática de carga (round-robin)
- ✅ Métricas instrumentadas (Datadog/Micrometer)

### Flujo de Procesamiento

```
POST /api/pdis → Guarda PDI → Publica ID en RabbitMQ
                                     ↓
                         [pdi_cola_procesador]
                                     ↓
                    ┌────────────────┼────────────────┐
                    ▼                ▼                ▼
                Worker 1         Worker 2         Worker N
                    │                │                │
                    ├─► OCR          ├─► OCR          ├─► OCR
                    ├─► Etiquetado   ├─► Etiquetado   ├─► Etiquetado
                    ├─► Guarda DB    ├─► Guarda DB    ├─► Guarda DB
                    └─► Evento       └─► Evento       └─► Evento
```

## 🚀 Inicio Rápido - Múltiples Workers

### Windows (PowerShell)

```powershell
# Levantar 2 workers locales
.\start-workers.ps1 -NumWorkers 2

# Probar con 10 PDIs
.\test-workers.ps1 -NumPdis 10
```

### Linux/Mac (Bash)

```bash
# Terminal 1: Worker 1
export SERVER_PORT=8083
./mvnw spring-boot:run

# Terminal 2: Worker 2
export SERVER_PORT=8084
./mvnw spring-boot:run

# Terminal 3: Probar
./test-workers.sh 10
```

## 📚 Documentación Completa

Para información detallada sobre:
- Configuración de múltiples workers
- Opciones de despliegue (Render + locales)
- Monitoreo y métricas
- Troubleshooting

Ver: **[WORKERS_GUIDE.md](./WORKERS_GUIDE.md)** 📖

## ⚙️ Configuración

### Variables de Entorno Requeridas

```bash
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=procesador_pdi
DB_USER=postgres
DB_PASSWORD=yourpassword

# RabbitMQ (compartido entre todos los workers)
RABBITMG_HOST=your-rabbitmq-host.cloudamqp.com
RABBITMG_PORT=5671
RABBITMG_USRNME=your-username
RABBITMG_PSW=your-password
RABBITMG_VHOST=your-vhost

# ApiLayer (etiquetado IA)
APILAYER_KEY=your-api-key

# Puerto del servidor (cambiar para cada worker)
SERVER_PORT=8083
```

## 📊 Métricas Disponibles

- `dds.pdi.processed` - Contador de PDIs procesados (tags: status, has_ocr, has_tags)
- `dds.pdi.process.latency` - Tiempo de procesamiento
- `dds.pdi.tags.count` - Número de etiquetas generadas por PDI

## 🧪 Validación del Sistema

### Checklist de Workers

- [ ] Cola `pdi_cola_procesador` creada en RabbitMQ
- [ ] Worker 1 conectado (log muestra "Listening on queue")
- [ ] Worker 2 conectado (log muestra "Listening on queue")
- [ ] Crear 5 PDIs → Ambos workers procesan mensajes
- [ ] Logs muestran distribución entre workers (no todos a uno solo)
- [ ] PDIs guardados con `etiquetas_auto` y `ocrTexto`
- [ ] Evento `PDI_PROCESADO` emitido correctamente

### Ejemplo de Prueba Manual

```bash
# Crear un PDI
curl -X POST http://localhost:8083/api/pdis \
  -H "Content-Type: application/json" \
  -d '{
    "hechoId": "hecho-test-1",
    "descripcion": "Prueba de worker",
    "lugar": "Buenos Aires",
    "momento": "2025-11-14T10:00:00",
    "contenido": "Contenido de prueba",
    "imagenUrl": "https://picsum.photos/400/300"
  }'

# Esperar 5-10 segundos (procesamiento en background)

# Consultar PDI procesado
curl http://localhost:8083/api/pdis/1

# Respuesta esperada:
# {
#   "id": "1",
#   "ocrTexto": "Texto extraído...",
#   "etiquetas_auto": ["nature", "landscape", "outdoor"]
# }
```

## 🎯 Características Implementadas

- ✅ Sistema de workers con RabbitMQ
- ✅ Cola persistente y durable
- ✅ Procesamiento único garantizado
- ✅ Procesamiento asíncrono (OCR + IA)
- ✅ Métricas instrumentadas
- ✅ Emisión de eventos (consistencia eventual)
- ✅ Escalabilidad horizontal
- ✅ Scripts de testing automatizados

---

**Autor**: Franco Smuraglia  
**Materia**: Diseño de Sistemas (DDS) 2025  
**Universidad**: UTN
