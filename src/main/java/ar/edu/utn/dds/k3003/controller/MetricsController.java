package ar.edu.utn.dds.k3003.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin/metrics")
public class MetricsController {

    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);
    private final AtomicInteger debugGauge = new AtomicInteger(0);
    private final MeterRegistry meterRegistry;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public MetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        meterRegistry.gauge("pdi.debug.gauge", debugGauge);
        meterRegistry.gauge("pdi.total.count", this, ctrl -> ctrl.countPdis());
        log.info("✅ MetricsController inicializado para métricas de PdI");
    }

    public double countPdis() {
        return ((Number) entityManager.createQuery("SELECT COUNT(p) FROM PdI p").getSingleResult()).doubleValue();
    }

    @GetMapping("/pdis/total")
    public ResponseEntity<Map<String, Object>> getTotalPdis() {
        double total = countPdis();
        return ResponseEntity.ok(Map.of("totalPdis", (int) total));
    }

    // Total de PdIs procesadas
    @GetMapping("/pdis/procesadas")
    public ResponseEntity<Map<String, Object>> getTotalPdisProcesadas() {
        Gauge gauge = meterRegistry.find("pdi.procesadas.count").gauge();
        double total = gauge != null ? gauge.value() : 0;
        return ResponseEntity.ok(Map.of("totalPdisProcesadas", (int) total));
    }

    // PdIs por hecho
    @GetMapping("/pdis/por-hecho")
    public ResponseEntity<Map<String, Object>> getPdisPorHecho() {
        Collection<Gauge> gauges = meterRegistry.get("pdi.pdis.por.hecho").gauges();
        Map<String, Integer> pdisPorHecho = new HashMap<>();
        for (Gauge g : gauges) {
            String hecho = g.getId().getTag("hecho");
            pdisPorHecho.put(hecho, (int) g.value());
        }
        return ResponseEntity.ok(Map.of("pdisPorHecho", pdisPorHecho));
    }

    // Actividad reciente (contadores de operaciones)
    @GetMapping("/actividad")
    public ResponseEntity<Map<String, Object>> getActividad() {
        Map<String, Object> actividad = new HashMap<>();
        actividad.put("pdis_creadas", getCounterValue("pdi.pdis", "operation", "crear"));
        actividad.put("pdis_procesadas", getCounterValue("pdi.pdis", "operation", "procesar"));
        actividad.put("pdis_buscadas", getCounterValue("pdi.pdis", "operation", "buscar"));
        return ResponseEntity.ok(actividad);
    }

    private double getCounterValue(String name, String... tags) {
        Counter counter = meterRegistry.find(name).tags(tags).counter();
        return counter != null ? counter.count() : 0;
    }

    // Endpoint para cambiar el valor del gauge de debug
    @GetMapping("/gauge/{value}")
    public ResponseEntity<String> updateDebugGauge(@PathVariable Integer value) {
        debugGauge.set(value);
        log.info("🔧 Valor gauge cambiado a: {}", value);
        return ResponseEntity.ok("updated gauge: " + value);
    }
}