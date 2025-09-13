package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SolicitudesRetrofitClient {
    @GET("/api/solicitudes/hecho/{id}/estaActivo")
    Call<Boolean> estaActivo(@Path("id")String id );
}
