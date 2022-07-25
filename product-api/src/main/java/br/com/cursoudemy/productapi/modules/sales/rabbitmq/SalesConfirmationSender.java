package br.com.cursoudemy.productapi.modules.sales.rabbitmq;

import br.com.cursoudemy.productapi.modules.sales.dto.SalesConfirmationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SalesConfirmationSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app-config.rabbit.exchange.product}")
    private String productTopicExchange;

    @Value("${app-config.rabbit.routingKey.sales-confirmation}")
    private String salesConfirmationKey;

    public void sendSalesConfirmationMessage(SalesConfirmationDTO message) {
        try {
            log.info("Sendind message: {}", new ObjectMapper().writeValueAsString(message));
            this.rabbitTemplate.convertAndSend(this.productTopicExchange, this.salesConfirmationKey, message);
            log.info("Message was sent successfully!");
        } catch (Exception exception) {
            log.info("Error while trying to send sales confirmation message: ", exception);
        }
    }
}
