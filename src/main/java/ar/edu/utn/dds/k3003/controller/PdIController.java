package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
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
    public ResponseEntity<List<PdIDTO>> listarPdis(){
        //return ResponseEntity.ok(fachadaProcesadorPdI.listarTodos());
        return null;
    }

    @PostMapping("/pdis")
    public ResponseEntity<PdIDTO> crearPdI(@RequestBody PdIDTO pdIDTO){
        return ResponseEntity.ok(fachadaProcesadorPdI.procesar(pdIDTO));
    }

    @GetMapping("/pdis/{id}")
    public ResponseEntity<PdIDTO> buscarPorId(@PathVariable String id){
        return ResponseEntity.ok(fachadaProcesadorPdI.buscarPdIPorId(id));
    }

    @GetMapping("/hechos/{id}/pdis")
    public ResponseEntity<List<PdIDTO>> buscarPorHecho(@PathVariable String hecho){
        return ResponseEntity.ok(fachadaProcesadorPdI.buscarPorHecho(hecho));
    }

    @DeleteMapping("/pdis")
    public ResponseEntity<Void> borrarTodo() {
        fachadaProcesadorPdI.borrarTodo();
        return ResponseEntity.noContent().build();
    }
}