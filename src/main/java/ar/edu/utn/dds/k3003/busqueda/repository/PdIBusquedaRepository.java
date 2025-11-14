package ar.edu.utn.dds.k3003.busqueda.repository;

import ar.edu.utn.dds.k3003.busqueda.document.PdIBusqueda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PdIBusquedaRepository extends MongoRepository<PdIBusqueda, String> {

    /**
     * Búsqueda full-text usando el índice en textoBusqueda.
     */
    @Query("{ $text: { $search: ?0 } }")
    Page<PdIBusqueda> searchByText(String text, Pageable pageable);

    /**
     * Buscar PDIs por hechoId.
     */
    @Query("{ 'hechoId': ?0 }")
    Page<PdIBusqueda> findByHechoId(Integer hechoId, Pageable pageable);
}
