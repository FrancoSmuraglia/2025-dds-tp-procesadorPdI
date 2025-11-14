package ar.edu.utn.dds.k3003.busqueda.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "ar.edu.utn.dds.k3003.busqueda.repository",
        mongoTemplateRef = "searchMongoTemplate"
)
public class MongoSearchConfig {

    @Value("${SEARCH_DB_MONGO_URI}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        return MongoClients.create(connectionString);
    }

    @Bean(name = "searchMongoTemplate")
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "Busquedas");
    }
}

