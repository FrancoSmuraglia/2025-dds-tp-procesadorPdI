package ar.edu.utn.dds.k3003.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SolicitudesRetrofitClient {
    @GET("/api/solicitudes/hechos/{id}/estaActivo")
    Call<Boolean> estaActivo(@Path("id")String id );
}
