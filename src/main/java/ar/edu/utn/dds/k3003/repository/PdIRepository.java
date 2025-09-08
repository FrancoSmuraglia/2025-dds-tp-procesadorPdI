package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.model.PdI;

import java.util.List;
import java.util.Optional;

public interface PdIRepository {

    Optional<PdI> findById(Integer id);
    Optional<List<PdI>> findByHechoId(String id);
    PdI save(PdI pdi);
    List<PdI> findAll();
}
