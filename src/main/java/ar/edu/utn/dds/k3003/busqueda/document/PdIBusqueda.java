package ar.edu.utn.dds.k3003.busqueda.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pdis_busqueda")
@CompoundIndex(name = "texto_pdi_busqueda_index", def = "{'textoBusqueda': 'text'}")
public class PdIBusqueda {

    @Id
    private String id;               // ID del PDI en SQL

    private String hechoId;         // ID del Hecho al que pertenece

    private String descripcion;
    private String contenido;
    private String ocrTexto;

    private List<String> etiquetasAuto;

    private String textoBusqueda;   // Todo el texto indexado

    private Instant ultimoUpdate;
}
