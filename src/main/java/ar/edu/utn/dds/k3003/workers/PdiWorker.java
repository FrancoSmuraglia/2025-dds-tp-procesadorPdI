package ar.edu.utn.dds.k3003.workers;

import ar.edu.utn.dds.k3003.config.RabbitConfig;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.PdIRepository;
import ar.edu.utn.dds.k3003.service.ProcesadorService;
import ar.edu.utn.dds.k3003.busqueda.services.IndexadorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PdiWorker {

    private final ProcesadorService procesadorService;
    private final PdIRepository pdIRepository;
    private final IndexadorService indexadorService;

    public PdiWorker(ProcesadorService procesadorService,
                     PdIRepository pdIRepository,
                     IndexadorService indexadorService) {
        this.procesadorService = procesadorService;
        this.pdIRepository = pdIRepository;
        this.indexadorService = indexadorService;
    }

    /**
     * Escucha la cola y procesa los PDIs entrantes
     * Cada PdI se procesa UNA SOLA VEZ
     */
    @RabbitListener(queues = RabbitConfig.PDI_COLA_PROCESADOR)
    public void procesarPdI(String mensaje) {
        try {
            Integer pdiId = Integer.parseInt(mensaje);

            // Buscar el PDI
            PdI pdi = pdIRepository.findById(pdiId)
                    .orElseThrow(() -> new RuntimeException("Pdi no encontrado con ID: " + pdiId));

            System.out.println("Procesando PdI recibido: " + pdi.getId());

            // Procesar OCR + etiquetas automáticas
            procesadorService.procesar(pdi);

            // Guardar cambios en SQL
            pdIRepository.save(pdi);

            System.out.println("PdI procesado y guardado correctamente " + pdi.getId());

            // 🔥 INDEXAR EN MONGODB (luego del guardado y procesamiento)
            indexadorService.indexarPdi(pdi.getId());

            System.out.println("PdI indexado correctamente en MongoDB: " + pdi.getId());

        } catch (Exception e) {
            System.err.println("Error al procesar PdI " + mensaje + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
