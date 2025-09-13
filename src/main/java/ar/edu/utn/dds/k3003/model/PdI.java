package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class PdI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String hechoId; // Debería ser una clase Hecho
    private String descripcion;
    private String lugar;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime momento;
    private String contenido;

    @ElementCollection
    @CollectionTable(name = "pdi_etiquetas", joinColumns = @JoinColumn(name = "pdi_id"))
    @Column(name = "etiqueta")
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
    public PdI(String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas) {
        this.hechoId = hechoId;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.momento = momento;
        this.contenido = contenido;
        this.etiquetas = etiquetas;
    }
}
