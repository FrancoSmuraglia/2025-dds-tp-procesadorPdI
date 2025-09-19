package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.client.SolicitudesProxy;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.InMemoryPdIRepo;
import ar.edu.utn.dds.k3003.repository.PdIRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class Fachada implements FachadaProcesadorPdI{

    private int idCounter = 1;

    private final PdIRepository pdIRepository;
    private SolicitudesProxy solicitudesProxy;

    public Fachada(){
        this.pdIRepository = new InMemoryPdIRepo();
        this.solicitudesProxy = null;
    }

    @Autowired
    public Fachada(PdIRepository pdIRepository, SolicitudesProxy solicitudesProxy) {
        this.pdIRepository = pdIRepository;
        this.solicitudesProxy = solicitudesProxy;
    }

    public int generarNuevoId(){
        return idCounter++;
    }
    
    @Override
    public PdIDTO procesar(PdIDTO pdi) throws IllegalStateException {
        var pdisExistentes = this.pdIRepository.findByHechoId(pdi.hechoId());
        boolean pdiYaExiste = pdisExistentes
                .map(lista -> lista.stream()
                        .anyMatch(x -> x.getContenido().equals(pdi.contenido())))
                .orElse(false);
        if (pdiYaExiste){
            return pdi;
        }
        if (solicitudesProxy.estaActivo(pdi.hechoId())){
            val pdiNuevo = new PdI(this.generarNuevoId(), pdi.hechoId(), pdi.descripcion(), pdi.lugar(), pdi.momento(), pdi.contenido(), pdi.etiquetas());
            this.pdIRepository.save(pdiNuevo);
            return new PdIDTO(pdiNuevo.getId().toString(), pdiNuevo.getHechoId());
        }
        else {
            throw new IllegalStateException(pdi.hechoId() + " está censurado");
        }
    }

    @Override
    public PdIDTO buscarPdIPorId(String pdiId) throws NoSuchElementException {
        return this.pdIRepository.findById(Integer.parseInt(pdiId))
                .map(pdi -> new PdIDTO(
                        pdi.getId().toString(),
                        pdi.getHechoId(),
                        pdi.getDescripcion(),
                        pdi.getLugar(),
                        pdi.getMomento(),
                        pdi.getContenido(),
                        pdi.getEtiquetas()
                ))
                .orElseThrow(() -> new NoSuchElementException("El PdI con Id " + pdiId + " no existe"));
    }

    @Override
    public List<PdIDTO> buscarPorHecho(String hechoId) throws NoSuchElementException {
        if (this.pdIRepository.findByHechoId(hechoId).isPresent()){
            return pdIRepository.findByHechoId(hechoId).get()
                    .stream()
                    .map(pdi -> new PdIDTO(pdi.getId().toString(), pdi.getHechoId()))
                    .collect(Collectors.toList());
        }
        else {
            throw new NoSuchElementException("No hay un hecho con id: " + hechoId);
        }
    }

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
        if (this.solicitudesProxy == null){
            this.solicitudesProxy = solicitudesProxy;
        }
    }

    public List<PdIDTO> listarTodos() {
        return this.pdIRepository.findAll().stream()
                .map(pdi -> new PdIDTO(
                        pdi.getId().toString(),
                        pdi.getHechoId(),
                        pdi.getDescripcion(),
                        pdi.getLugar(),
                        pdi.getMomento(),
                        pdi.getContenido(),
                        pdi.getEtiquetas()
                ))
                .collect(Collectors.toList());
    }

    public void borrarTodo() {
        pdIRepository.deleteAll();
    }
}
