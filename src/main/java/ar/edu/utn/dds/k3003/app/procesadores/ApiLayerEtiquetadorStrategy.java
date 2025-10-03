package ar.edu.utn.dds.k3003.app.procesadores;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ApiLayerEtiquetadorStrategy implements EtiquetadorStrategy{

    private final RestTemplate restTemplate;
    private final String apiKey = "H6XHFvHI7Me7ivQNFsfVyAGwLVfv8G1p";

    public ApiLayerEtiquetadorStrategy(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> generarEtiquetas(String imagenUrl) {
        String url = "https://api.apilayer.com/image_labeling/url?url=" + imagenUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String bodyStr = response.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            // Mapea directamente a lista
            List<Map<String, Object>> results = mapper.readValue(bodyStr, List.class);

            List<String> etiquetas = new ArrayList<>();
            for (Map<String, Object> item : results) {
                etiquetas.add(item.get("label").toString());
            }
            return etiquetas;
        } catch (Exception e) {
            System.err.println("Error al parsear API Layer: " + bodyStr);
            return Collections.emptyList();
        }
    }
}
