package ar.edu.utn.dds.k3003.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // Nombre de la cola
    public static final String PDI_COLA_PROCESADOR = "pdi_cola_procesador";

    // Cola principal
    @Bean
    public Queue pdiColaProcesador() {
        return new Queue(PDI_COLA_PROCESADOR, true);
    }

    // RabbitAdmin, para crear automáticamente colas declaradas
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // Para publicar los mensajes en la cola apenas llegan desde el POST a /api/pdis en el metodo de la fachada
    @Bean
    public org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new org.springframework.amqp.rabbit.core.RabbitTemplate(connectionFactory);
    }

    /**
     * Inicializa el RabbitAdmin al iniciar la aplicación.
     * Garantiza que las colas declaradas arriba se creen automáticamente.
     */
    @Bean
    public org.springframework.boot.ApplicationRunner runner(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }
}
