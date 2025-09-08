package ar.edu.utn.dds.k3003.service;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Profile("!test")
public class FachadaSolicitudesDummy implements FachadaSolicitudes {
    @Override
    public SolicitudDTO agregar(SolicitudDTO solicitudDTO) {
        return null;
    }

    @Override
    public SolicitudDTO modificar(String solicitudId, EstadoSolicitudBorradoEnum esta, String descripcion) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<SolicitudDTO> buscarSolicitudXHecho(String hechoId) {
        return List.of();
    }

    @Override
    public SolicitudDTO buscarSolicitudXId(String solicitudId) {
        return null;
    }

    @Override
    public boolean estaActivo(String hechoId) {
        return true;
    }

    @Override
    public void setFachadaFuente(FachadaFuente fuente) {

    }
}
