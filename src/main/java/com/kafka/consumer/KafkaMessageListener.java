package com.kafka.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class KafkaMessageListener implements AcknowledgingMessageListener<String, Message> {

//    @KafkaListener(topics = {"message_creation"})
//    public void OnMessage(ConsumerRecord<String, Message> rc){
//        log.info(rc.value().getAddress());
//        rc.headers().forEach(h -> log.info(new String(h.value())));
//    }

    @Override
    @KafkaListener(topics = {"message_creation"})
    public void onMessage(ConsumerRecord<String, Message> rc, Acknowledgment acknowledgment) {
        log.info("Manual {} ",rc.value().getAddress());
        rc.headers().forEach(h -> log.info(new String(h.value())));
        acknowledgment.acknowledge();
    }
}
