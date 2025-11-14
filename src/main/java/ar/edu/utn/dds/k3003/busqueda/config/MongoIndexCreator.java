package ar.edu.utn.dds.k3003.busqueda.config;

import ar.edu.utn.dds.k3003.busqueda.document.PdIBusqueda;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MongoIndexCreator {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoIndexCreator(@Qualifier("searchMongoTemplate") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void init() {
        log.info("Asegurando creación de índices de texto para PdiBusqueda...");

        try {
            IndexOperations indexOps = mongoTemplate.indexOps(PdIBusqueda.class);

            // Crear índice de texto para búsqueda full text
            TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                    .onField("descripcion")
                    .onField("ocr")
                    .onField("etiquetas")
                    .build();

            indexOps.ensureIndex(textIndex);
            log.info("Índice de texto para PdiBusqueda creado correctamente.");

        } catch (Exception e) {
            log.error("Error al crear índice de texto para PdiBusqueda", e);
        }
    }
}

