package ar.edu.utn.dds.k3003.workers;

import ar.edu.utn.dds.k3003.config.RabbitConfig;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.PdIRepository;
import ar.edu.utn.dds.k3003.service.ProcesadorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PdiWorker {
    private final ProcesadorService procesadorService;
    private final PdIRepository pdIRepository;

    public PdiWorker(ProcesadorService procesadorService, PdIRepository pdIRepository) {
        this.procesadorService = procesadorService;
        this.pdIRepository = pdIRepository;
    }

    /**
     * Escucha la cola y procesa los PDIs entrantes
     * Cada PdI se procesa UNA SOLA VEZ
     */

    @RabbitListener(queues = RabbitConfig.PDI_COLA_PROCESADOR)
    public void procesarPdI(PdI pdi) {
        try {
            System.out.println("Procesando PdI recibido: " + pdi.getId());

            // Procesamiento de OCR + etiquetas
            procesadorService.procesar(pdi);

            // Guardar en base de datos
            pdIRepository.save(pdi);

            System.out.println("PdI procesado y guardado correctamente " + pdi.getId());
        } catch (Exception e) {
            System.err.println("Error al procesar PdI " + pdi.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
