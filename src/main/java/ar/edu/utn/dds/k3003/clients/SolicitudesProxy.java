package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SolicitudesProxy  {

    final private String endpoint;
    private final SolicitudesRetrofitClient service;

    public SolicitudesProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("Solicitudes", "https://grupo12-solicitudes.onrender.com");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(SolicitudesRetrofitClient.class);
    }

    public boolean estaActivo(String unHechoId) {
        try {
            Response<Boolean> response = service.estaActivo(unHechoId).execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
                // If the fact is not found, it's not active in this context.
                return false;
            }
            throw new RuntimeException("Error conectándose con el componente de solicitudes.");
        } catch (IOException e) {
            throw new RuntimeException("Error de I/O al conectarse con el componente de solicitudes.", e);
        }

    }
       public SolicitudDTO agregar(SolicitudDTO solicitudDTO) {
        return null;
    }

       public SolicitudDTO modificar(String solicitudId, EstadoSolicitudBorradoEnum esta, String descripcion) throws NoSuchElementException {
        return null;
    }

       public List<SolicitudDTO> buscarSolicitudXHecho(String hechoId) {
        return List.of();
    }

       public SolicitudDTO buscarSolicitudXId(String solicitudId) {
        return null;
    }

       public void setFachadaFuente(FachadaFuente fuente) {

    }
}
