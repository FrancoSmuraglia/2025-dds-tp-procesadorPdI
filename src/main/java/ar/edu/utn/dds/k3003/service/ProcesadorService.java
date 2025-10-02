package ar.edu.utn.dds.k3003.service;

import ar.edu.utn.dds.k3003.app.procesadores.EtiquetadorStrategy;
import ar.edu.utn.dds.k3003.app.procesadores.OcrStrategy;
import ar.edu.utn.dds.k3003.model.PdI;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcesadorService {

    private final OcrStrategy ocrStrategy;
    private final EtiquetadorStrategy etiquetadorStrategy;

    public ProcesadorService(OcrStrategy ocrStrategy, EtiquetadorStrategy etiquetadorStrategy){
        this.ocrStrategy = ocrStrategy;
        this.etiquetadorStrategy = etiquetadorStrategy;
    }

    public void procesar(PdI pdI){
        if (pdI.getImagenUrl() != null && !pdI.getImagenUrl().isBlank()){
            String ocrTexto = ocrStrategy.extraerTexto(pdI.getImagenUrl());
            pdI.setOcrTexto(ocrTexto);

            List<String> etiquetas = etiquetadorStrategy.generarEtiquetas(pdI.getImagenUrl());
            pdI.setEtiquetasAuto(etiquetas);
        }
    }
}
