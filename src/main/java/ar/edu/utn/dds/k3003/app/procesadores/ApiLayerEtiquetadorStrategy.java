package ar.edu.utn.dds.k3003.app.procesadores;

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

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
        List<Map<String, Object>> result = response.getBody();

        if (result == null){
            return Collections.emptyList();
        }

        List<String> etiquetas = new ArrayList<>();
        for (Map<String, Object> item : result) {
            etiquetas.add(item.get("label").toString());
        }
        return etiquetas;
    }
}
