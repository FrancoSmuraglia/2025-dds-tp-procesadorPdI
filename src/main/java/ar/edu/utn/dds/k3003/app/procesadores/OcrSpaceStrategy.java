package ar.edu.utn.dds.k3003.app.procesadores;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map body = response.getBody();

        try{
            return ((Map)((java.util.List) body.get("ParsedResults")).get(0)).get("ParsedText").toString();
        } catch (Exception e){
            return "Error en la extracción de texto OCR";
        }
    }
}
