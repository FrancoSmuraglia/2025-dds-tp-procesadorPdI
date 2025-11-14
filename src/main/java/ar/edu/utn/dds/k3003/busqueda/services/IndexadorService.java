package ar.edu.utn.dds.k3003.busqueda.services;

public interface IndexadorService {

    /**
     * Indexa un PDI procesado en MongoDB
     * @param pdiId ID del PDI procesado (SQL)
     */
    void indexarPdi(Integer pdiId);
}
