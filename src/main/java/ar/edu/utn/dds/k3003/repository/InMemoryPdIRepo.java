package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.model.PdI;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("test")
public class InMemoryPdIRepo implements PdIRepository{

    private List<PdI> pdis;

    public InMemoryPdIRepo(){
        this.pdis = new ArrayList<>();
    }
    @Override
    public Optional<PdI> findById(Integer id) {
        return this.pdis.stream().filter(x -> x.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<List<PdI>> findByHechoId(String id) {
        List<PdI> resultado = this.pdis.stream()
                .filter(pdi -> pdi.getHechoId().equals(id))
                .toList();
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado);
    }

    @Override
    public PdI save(PdI pdi) {
        this.pdis.add(pdi);
        return pdi;
    }

    @Override
    public List<PdI> findAll(){
        return new ArrayList<>(pdis);
    }
}