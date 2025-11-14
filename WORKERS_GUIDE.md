# Sistema de Workers para Procesamiento de PDIs

## 📋 Descripción

Este módulo implementa un **sistema de workers distribuidos** para el procesamiento asíncrono de PDIs (Piezas de Información). El procesamiento incluye:
- **OCR** (extracción de texto de imágenes)
- **Etiquetado automático** usando IA (ApiLayer Image Labeling)

## 🏗️ Arquitectura

```
┌─────────────────┐
│   Fachada.java  │ ──► POST /api/pdis
└────────┬────────┘
         │ 1. Guarda PDI en DB
         │ 2. Publica ID en cola RabbitMQ
         ▼
┌──────────────────────┐
│  pdi_cola_procesador │ (Cola persistente)
└──────────┬───────────┘
           │
           ├──────► Worker 1 (@RabbitListener)
           ├──────► Worker 2 (@RabbitListener)  
           └──────► Worker N (@RabbitListener)
                    │
                    ├─► ProcesadorService.procesar()
                    ├─► OCR + Etiquetado
                    ├─► Guarda en DB
                    └─► Emite evento PDI_PROCESADO
```

## ✅ Características Implementadas

### 1. Cola de Trabajo Persistente
- **Cola**: `pdi_cola_procesador`
- **Durabilidad**: `true` (sobrevive a reinicios de RabbitMQ)
- **Garantía**: Cada PDI se procesa **UNA SOLA VEZ** (acknowledgment automático)

### 2. Worker con @RabbitListener
```java
@RabbitListener(queues = RabbitConfig.PDI_COLA_PROCESADOR)
public void procesarPdI(String mensaje) {
    // Procesa el PDI identificado por su ID
}
```

### 3. Procesamiento Asíncrono
- El endpoint POST /api/pdis responde inmediatamente después de guardar
- El procesamiento OCR + etiquetado ocurre en background
- Respuesta inicial: `"OCR Pendiente de procesamiento"`

### 4. Métricas Instrumentadas
- `dds.pdi.processed` (counter) - PDIs procesados
- `dds.pdi.process.latency` (timer) - Tiempo de procesamiento
- `dds.pdi.tags.count` (summary) - Etiquetas generadas

### 5. Consistencia Eventual
- Al completar el procesamiento, emite evento `PDI_PROCESADO`
- El módulo agregador escucha y re-indexa en MongoDB con los nuevos tags

## 🚀 Cómo Probar con Múltiples Workers

### Opción 1: Instancias Locales Múltiples

#### Paso 1: Configurar Variables de Entorno

Crea un archivo `.env` o configura las siguientes variables:

```bash
# Base de datos (puede ser compartida o independiente por worker)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=procesador_pdi
DB_USER=postgres
DB_PASSWORD=yourpassword

# RabbitMQ (DEBE SER COMPARTIDO entre todos los workers)
RABBITMG_HOST=your-rabbitmq-host.cloudamqp.com
RABBITMG_PORT=5671
RABBITMG_USRNME=your-username
RABBITMG_PSW=your-password
RABBITMG_VHOST=your-vhost

# URLs de otros servicios
URL_FUENTE=http://localhost:8081
URL_SOLICITUDES=http://localhost:8082
URL_AGREGADOR=http://localhost:8080

# ApiLayer (para etiquetado de imágenes)
APILAYER_KEY=your-api-key

# Puerto del servidor (cambiar para cada worker)
SERVER_PORT=8083
```

#### Paso 2: Levantar Worker 1

```bash
cd 2025-dds-tp-procesadorPdI

# Usar puerto 8083
export SERVER_PORT=8083  # Linux/Mac
# o
set SERVER_PORT=8083     # Windows CMD
# o
$env:SERVER_PORT=8083    # Windows PowerShell

./mvnw spring-boot:run
```

**Log esperado:**
```
Listening on queue: pdi_cola_procesador
Worker 1 ready to process PDIs
```

#### Paso 3: Levantar Worker 2 (en otra terminal)

```bash
cd 2025-dds-tp-procesadorPdI

# Usar puerto 8084 (diferente al Worker 1)
export SERVER_PORT=8084  # Linux/Mac
# o
set SERVER_PORT=8084     # Windows CMD
# o
$env:SERVER_PORT=8084    # Windows PowerShell

./mvnw spring-boot:run
```

**Log esperado:**
```
Listening on queue: pdi_cola_procesador
Worker 2 ready to process PDIs
```

### Opción 2: Worker en Render + Workers Locales

#### Worker en Render (ya desplegado)
- URL: `https://your-app.onrender.com`
- Escucha automáticamente la cola `pdi_cola_procesador`

#### Workers Locales (1 o más)
```bash
# Worker Local 1 - Puerto 8083
SERVER_PORT=8083 ./mvnw spring-boot:run

# Worker Local 2 - Puerto 8084  
SERVER_PORT=8084 ./mvnw spring-boot:run
```

**IMPORTANTE:** Todos los workers DEBEN apuntar al **mismo RabbitMQ** (mismas credenciales).

## 🧪 Pruebas de Distribución de Carga

### Escenario 1: Creación de Múltiples PDIs

```bash
# Crear 10 PDIs rápidamente
for i in {1..10}; do
  curl -X POST http://localhost:8083/api/pdis \
    -H "Content-Type: application/json" \
    -d '{
      "hechoId": "hecho-'$i'",
      "descripcion": "PDI de prueba '$i'",
      "lugar": "Test Location",
      "momento": "2025-11-14T10:00:00",
      "contenido": "Contenido del PDI",
      "imagenUrl": "https://example.com/image'$i'.jpg"
    }'
  echo "PDI $i creado"
done
```

### Escenario 2: Observar Logs de Distribución

**Worker 1 (Puerto 8083):**
```
Procesando PdI recibido: 1
Procesando PdI recibido: 3
Procesando PdI recibido: 5
...
```

**Worker 2 (Puerto 8084):**
```
Procesando PdI recibido: 2
Procesando PdI recibido: 4
Procesando PdI recibido: 6
...
```

RabbitMQ distribuye automáticamente los mensajes entre los workers disponibles (**round-robin**).

### Escenario 3: Verificar Procesamiento

```bash
# Consultar un PDI procesado
curl http://localhost:8083/api/pdis/1

# Respuesta esperada:
{
  "id": "1",
  "hechoId": "hecho-1",
  "descripcion": "PDI de prueba 1",
  "ocrTexto": "Texto extraído por OCR...",
  "etiquetas_auto": ["building", "architecture", "outdoor"]
}
```

## 📊 Monitoreo de Workers

### RabbitMQ Management UI

Accede al panel de RabbitMQ para ver:
- **Consumers**: Número de workers conectados
- **Messages Ready**: PDIs pendientes de procesar
- **Message Rate**: Velocidad de procesamiento
- **Acknowledge Rate**: PDIs procesados exitosamente

URL: `https://your-rabbitmq-host.cloudamqp.com` (según tu proveedor)

### Métricas en Datadog

Si Datadog está configurado:
- `dds.pdi.processed` - Contador por worker
- `dds.pdi.process.latency` - Latencia promedio

## 🔧 Configuración Avanzada

### Concurrencia por Worker

Para aumentar el número de threads por worker:

```properties
# application.properties
spring.rabbitmq.listener.simple.concurrency=5
spring.rabbitmq.listener.simple.max-concurrency=10
```

Esto permite que **cada worker** procese hasta 10 PDIs simultáneamente.

### Prefetch Count

Controla cuántos mensajes RabbitMQ envía a cada worker antes de esperar acknowledgment:

```properties
spring.rabbitmq.listener.simple.prefetch=1
```

- `prefetch=1`: Distribuye equitativamente (ideal para tareas largas)
- `prefetch=10`: Mayor throughput (ideal para tareas cortas)

### Dead Letter Queue (DLQ)

Para manejar PDIs que fallan repetidamente:

```java
@Bean
public Queue pdiColaProcesador() {
    return QueueBuilder.durable(PDI_COLA_PROCESADOR)
        .withArgument("x-dead-letter-exchange", "dlx-exchange")
        .withArgument("x-dead-letter-routing-key", "pdi.failed")
        .build();
}
```

## 🐛 Troubleshooting

### Worker no recibe mensajes

1. Verificar conexión a RabbitMQ:
```bash
# Revisar logs
tail -f logs/spring.log | grep RabbitMQ
```

2. Verificar que la cola existe:
```bash
# En RabbitMQ Management UI
Queues → pdi_cola_procesador → Debe tener consumers > 0
```

### PDIs quedan "Pendientes de procesamiento"

1. Verificar que al menos 1 worker está corriendo
2. Revisar logs del worker por errores
3. Verificar que ApiLayer API key es válida

### Duplicación de procesamiento

**NO DEBERÍA OCURRIR** debido a acknowledgment automático. Si ocurre:
1. Verificar que no hay transacciones suspendidas en DB
2. Revisar configuración de RabbitMQ (no usar `autoAck=false`)

## 📝 Checklist de Validación

- [ ] Cola `pdi_cola_procesador` creada en RabbitMQ
- [ ] Worker 1 conectado (log: "Listening on queue...")
- [ ] Worker 2 conectado (log: "Listening on queue...")
- [ ] Crear 5 PDIs → Ambos workers procesan mensajes
- [ ] Verificar distribución en logs (no todos a un solo worker)
- [ ] PDIs guardados con `etiquetas_auto` y `ocrTexto` completos
- [ ] Evento `PDI_PROCESADO` emitido a `hechos-topic-exchange`
- [ ] Métricas `dds.pdi.processed` incrementan correctamente

## 🎯 Resumen

✅ **Sistema completamente implementado:**
- Cola persistente con RabbitMQ
- Worker con `@RabbitListener` que procesa una vez por PDI
- Procesamiento asíncrono (OCR + etiquetado IA)
- Métricas instrumentadas
- Emisión de eventos para consistencia eventual
- Listo para escalar horizontalmente (agregar más workers)

Para agregar más capacidad de procesamiento, simplemente **levanta más instancias** con el mismo código y configuración (diferente puerto). RabbitMQ se encarga de distribuir la carga automáticamente. 🚀
