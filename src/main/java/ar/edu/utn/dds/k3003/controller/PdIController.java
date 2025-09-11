package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pdis")
public class PdIController {

    private final FachadaProcesadorPdI fachadaProcesadorPdI;

    @Autowired
    public PdIController(FachadaProcesadorPdI fachadaProcesadorPdI) {
        this.fachadaProcesadorPdI = fachadaProcesadorPdI;
    }

    @GetMapping
    public ResponseEntity<List<PdIDTO>> listarPdis(){
        //return ResponseEntity.ok(fachadaProcesadorPdI.listarTodos());
        return null;
    }

    @PostMapping
    public ResponseEntity<PdIDTO> crearPdI(@RequestBody PdIDTO pdIDTO){
        return ResponseEntity.ok(fachadaProcesadorPdI.procesar(pdIDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PdIDTO> buscarPorId(@PathVariable String id){
        return ResponseEntity.ok(fachadaProcesadorPdI.buscarPdIPorId(id));
    }

    @GetMapping(params = "hecho")
    public ResponseEntity<List<PdIDTO>> buscarPorHecho(@RequestParam String hecho){
        return ResponseEntity.ok(fachadaProcesadorPdI.buscarPorHecho(hecho));
    }
}
