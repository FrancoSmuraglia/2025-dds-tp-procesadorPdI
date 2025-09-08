package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class PdI {
    @Id
    private Integer id;

    private String hechoId; // Debería ser una clase Hecho
    private String descripcion;
    private String lugar;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime momento;
    private String contenido;
    private List<String> etiquetas;

    public Boolean fueProcesada(){
        return !etiquetas.isEmpty();
    }

    public PdI(Integer id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas) {
        this.id = id;
        this.hechoId = hechoId;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.momento = momento;
        this.contenido = contenido;
        this.etiquetas = etiquetas;
    }
}
