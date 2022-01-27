package com.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"message_creation"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                                  "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                                  "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class MessageListenerIntegrationTest {

    final String topic = "message_creation";

    @Autowired
    KafkaTemplate<String, Message> template;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    KafkaMessageListener kafkaMessageListenerSpy;

    @BeforeEach
    void setUp() {
        // wait until all the topic partitions are assigned to the Consumer
        for(MessageListenerContainer endpoint : endpointRegistry.getAllListenerContainers()){
            ContainerTestUtils.waitForAssignment(endpoint, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test
    void listener() throws InterruptedException {

        // GIVEN
        // Produce a message on topic
        Message m = Message.builder()
               .name("john")
               .address("US")
               .phone("055")
               .isActive(true)
               .id(1)
               .build();
        template.send(topic, m);

        // WHEN
        // wait 3 seconds, so that message can be reached out to the consumer
        CountDownLatch l = new CountDownLatch(1);
        l.await(3, TimeUnit.SECONDS);

        // THEN
        verify(kafkaMessageListenerSpy, times(1)).onMessage(isA(ConsumerRecord.class), isA(Acknowledgment.class));


    }
}
