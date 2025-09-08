/*
package ar.edi.itn.dds.k3003.app;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.PdI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FachadaTest {
    PdI pdi1;
    PdI pdi2;
    PdIDTO pdIDTO1;
    PdIDTO pdiDTO2;
    Fachada fachada;
    List<String> etiquetas = new ArrayList<>();

    @Mock
    FachadaSolicitudes fachadaSolicitudes;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        fachada = new Fachada();
        Mockito.when(this.fachadaSolicitudes.estaActivo("1")).thenReturn(true);
        this.fachada.setFachadaSolicitudes(this.fachadaSolicitudes);
        etiquetas.add("Etiqueta1");
        etiquetas.add("Etiqueta2");
        pdi1 = new PdI(fachada.generarNuevoId(), "1", "UnaDescripcion", "UnLugar", LocalDateTime.now(), "UnContenido", etiquetas);
        pdi2 = new PdI(fachada.generarNuevoId(), "1", "OtraDescripcion", "OtroLugar", LocalDateTime.now(), "OtroContenido", etiquetas);
        pdIDTO1 = new PdIDTO(pdi1.getId().toString(), pdi1.getHechoId());
        pdiDTO2 = new PdIDTO(pdi2.getId().toString(), pdi2.getHechoId());
    }

    @Test
    void testProcesar(){
        this.fachada.procesar(pdIDTO1);
        this.fachada.procesar(pdiDTO2);

        assertDoesNotThrow(() -> fachada.procesar(pdIDTO1));
    }

    @Test
    void testBuscarPorHechoId(){
        this.fachada.procesar(pdIDTO1);
        this.fachada.procesar(pdiDTO2);

        assertEquals(2, fachada.buscarPorHecho(pdIDTO1.hechoId()).size());
    }
}
*/