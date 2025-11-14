package ar.edu.utn.dds.k3003.workers;

import ar.edu.utn.dds.k3003.config.RabbitConfig;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.PdIRepository;
import ar.edu.utn.dds.k3003.service.ProcesadorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PdiWorker {
    private final ProcesadorService procesadorService;
    private final PdIRepository pdIRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    public PdiWorker(ProcesadorService procesadorService, PdIRepository pdIRepository, 
                     RabbitTemplate rabbitTemplate, ObjectMapper objectMapper,
                     MeterRegistry meterRegistry) {
        this.procesadorService = procesadorService;
        this.pdIRepository = pdIRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Escucha la cola y procesa los PDIs entrantes
     * Cada PdI se procesa UNA SOLA VEZ
     */

    @RabbitListener(queues = RabbitConfig.PDI_COLA_PROCESADOR)
    public void procesarPdI(String mensaje) {
        // Iniciar temporizador para medir latencia de procesamiento
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Integer pdiId = Integer.parseInt(mensaje);

            // Buscar el PDI
            PdI pdi = pdIRepository.findById(pdiId).orElseThrow(() -> new RuntimeException("Pdi no encontrado con ID: " + pdiId));

            System.out.println("Procesando PdI recibido: " + pdi.getId());

            // Procesamiento de OCR + etiquetas
            procesadorService.procesar(pdi);

            // Guardar en base de datos
            pdIRepository.save(pdi);

            System.out.println("PdI procesado y guardado correctamente " + pdi.getId());
            
            // NUEVO: Emitir evento para que el agregador re-indexe con las nuevas etiquetas
            emitirPdiProcesado(pdi.getHechoId(), pdiId);
            
            // Registrar métricas de éxito
            int numEtiquetas = pdi.getEtiquetasAuto() != null ? pdi.getEtiquetasAuto().size() : 0;
            boolean tieneOcr = pdi.getOcrTexto() != null && !pdi.getOcrTexto().isBlank();
            
            meterRegistry.counter("dds.pdi.processed", 
                "status", "success",
                "has_ocr", String.valueOf(tieneOcr),
                "has_tags", String.valueOf(numEtiquetas > 0)
            ).increment();
            
            // Registrar número de etiquetas generadas
            meterRegistry.summary("dds.pdi.tags.count").record(numEtiquetas);
            
            // Registrar latencia de procesamiento
            sample.stop(meterRegistry.timer("dds.pdi.process.latency"));
            
        } catch (Exception e) {
            System.err.println("Error al procesar PdI " + mensaje + ": " + e.getMessage());
            e.printStackTrace();
            
            // Registrar error en métricas
            meterRegistry.counter("dds.pdi.processed", 
                "status", "error"
            ).increment();
        }
    }
    
    /**
     * Emite un evento cuando un PDI ha sido procesado con OCR y etiquetas automáticas.
     * El agregador escuchará este evento y re-indexará el hecho con los nuevos datos.
     */
    private void emitirPdiProcesado(String hechoId, Integer pdiId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "PDI_PROCESADO");
            event.put("hechoId", hechoId);
            event.put("pdiId", pdiId);
            event.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(
                    RabbitConfig.TOPIC_EXCHANGE_NAME,
                    "pdi.procesado",
                    json
            );

            System.out.println("✅ Evento PDI_PROCESADO emitido para hecho: " + hechoId + ", PDI: " + pdiId);
        } catch (JsonProcessingException e) {
            System.err.println("❌ Error al emitir evento PDI_PROCESADO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
