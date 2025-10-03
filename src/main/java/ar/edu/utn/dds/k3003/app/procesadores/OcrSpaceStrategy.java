package ar.edu.utn.dds.k3003.app.procesadores;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class OcrSpaceStrategy implements OcrStrategy{

    private final RestTemplate restTemplate;
    private final String apiKey = "K89295320288957";

    public OcrSpaceStrategy(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public String extraerTexto(String imagenUrl) {
        String url = "https://api.ocr.space/parse/imageurl?apikey=" + apiKey + "&url=" + imagenUrl;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String body = response.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> json = mapper.readValue(body, Map.class);
            return ((Map)((List) json.get("ParsedResults")).get(0)).get("ParsedText").toString();
        } catch (Exception e){
            System.err.println("Error al parsear OCR: " + body);
            return "Error en la extracción de texto OCR";
        }
    }
}
