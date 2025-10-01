package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.model.dtos.PdI_DTO;

import java.util.List;
import java.util.NoSuchElementException;

public interface Fachada_Procesador_PdI {

    PdI_DTO procesar(PdI_DTO pdi) throws IllegalStateException;

    PdI_DTO buscarPdIPorId(String pdiId) throws NoSuchElementException;

    List<PdI_DTO> buscarPorHecho(String hechoId)
            throws NoSuchElementException;

    void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes);

}
