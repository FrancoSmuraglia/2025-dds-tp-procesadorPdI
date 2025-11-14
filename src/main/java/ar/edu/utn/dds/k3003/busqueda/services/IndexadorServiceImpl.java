package ar.edu.utn.dds.k3003.busqueda.services;

import ar.edu.utn.dds.k3003.busqueda.document.PdIBusqueda;
import ar.edu.utn.dds.k3003.busqueda.repository.PdIBusquedaRepository;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.PdIRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IndexadorServiceImpl implements IndexadorService {

    private final PdIRepository pdiRepositorySQL;
    private final PdIBusquedaRepository pdiBusquedaRepository;

    public IndexadorServiceImpl(PdIRepository pdiRepositorySQL,
                                PdIBusquedaRepository pdiBusquedaRepository) {
        this.pdiRepositorySQL = pdiRepositorySQL;
        this.pdiBusquedaRepository = pdiBusquedaRepository;
    }

    @Override
    public void indexarPdi(Integer pdiId) {
        log.info("Indexando PDI {} en MongoDB…", pdiId);

        Optional<PdI> pdiOpt = pdiRepositorySQL.findById(pdiId);

        if (pdiOpt.isEmpty()) {
            log.error("No se pudo indexar PDI {}: no existe en SQL.", pdiId);
            return;
        }

        PdI pdi = pdiOpt.get();

        String textoBusqueda = String.join(" ",
                pdi.getDescripcion() != null ? pdi.getDescripcion() : "",
                pdi.getContenido() != null ? pdi.getContenido() : "",
                pdi.getOcrTexto() != null ? pdi.getOcrTexto() : "",
                pdi.getEtiquetasAuto() != null ?
                        String.join(" ", pdi.getEtiquetasAuto()) : ""
        );

        PdIBusqueda doc = pdiBusquedaRepository.findById(pdi.getId().toString())
                .orElse(new PdIBusqueda());

        doc.setId(pdi.getId().toString());
        doc.setHechoId(pdi.getHechoId());
        doc.setDescripcion(pdi.getDescripcion());
        doc.setContenido(pdi.getContenido());
        doc.setOcrTexto(pdi.getOcrTexto());

        doc.setEtiquetasAuto(
                pdi.getEtiquetasAuto() != null ?
                        pdi.getEtiquetasAuto().stream().collect(Collectors.toList())
                        : null
        );

        doc.setTextoBusqueda(textoBusqueda);
        doc.setUltimoUpdate(Instant.now());

        pdiBusquedaRepository.save(doc);

        log.info("PDI {} indexado correctamente en MongoDB.", pdiId);
    }
}
