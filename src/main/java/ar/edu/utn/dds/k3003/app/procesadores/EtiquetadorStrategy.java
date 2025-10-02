package ar.edu.utn.dds.k3003.app.procesadores;

import java.util.List;

public interface EtiquetadorStrategy {
    List<String> generarEtiquetas(String imagenUrl);
}
