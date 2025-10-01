package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.dtos.PdI_DTO;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class PdIController {

    private final Fachada fachadaProcesadorPdI;

    @Autowired
    public PdIController(Fachada fachadaProcesadorPdI) {
        this.fachadaProcesadorPdI = fachadaProcesadorPdI;
    }

    @GetMapping("/pdis")
    public ResponseEntity<List<PdI_DTO>> listarPdis(){
        return ResponseEntity.ok(fachadaProcesadorPdI.listarTodos());
    }

    @PostMapping("/pdis")
    public ResponseEntity<PdI_DTO> crearPdI(@RequestBody PdI_DTO pdIDTO){
        return ResponseEntity.ok(fachadaProcesadorPdI.procesar(pdIDTO));
    }

    @GetMapping("/pdis/{id}")
    public ResponseEntity<PdI_DTO> buscarPorId(@PathVariable String id){
        return ResponseEntity.ok(fachadaProcesadorPdI.buscarPdIPorId(id));
    }

    @GetMapping("/hechos/{hechoId}/pdis")
    public ResponseEntity<List<PdI_DTO>> buscarPorHecho(@PathVariable String hechoId){
        return ResponseEntity.ok(fachadaProcesadorPdI.buscarPorHecho(hechoId));
    }

    @DeleteMapping("/pdis")
    public ResponseEntity<Void> borrarTodo() {
        fachadaProcesadorPdI.borrarTodo();
        return ResponseEntity.noContent().build();
    }
}