# Spring boot Kafka Consumer (With Integration Test Cases)

* `mvn clean install` 
* `java -jar target/KafkaConsumer-0.0.1-SNAPSHOT.jar`


### Run Consumer

* If you use your own producer then pls make sure you update following properties of `application.yml`,

```
type:
 mapping: 'com.kafka.producer.Message:com.kafka.consumer.Message'
 trusted:
  packages: 'com.kafka.producer'

```

* Please also update the topic name for integration test case in `MessageListenerIntegrationTest`
* In case of any exception, by default, kafka consumer will retry 10 times, you can change it with following annotation. 

```
    @RetryableTopic(
            attempts = "3", // number of retries
            backoff = @Backoff(delay = 1000, multiplier = 2), // next try will be after 1000 ms and then it delay would get multiple by multiplier value after each failure
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
```
* If the message will be failed to delivered after the trying all the attempts then it would go to dlt (dead letter topic).

* If you want to use auto acknowledgment then replace Manual with Batch in `KafkaConfiguration` file,

```
factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

Replace it with following,

factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

```

And then replace the listen with following,

```
    @KafkaListener(topics = {"message_creation"})
    public void OnMessage(ConsumerRecord<String, Message> rc){
        log.info(rc.value().getAddress());
        rc.headers().forEach(h -> log.info(new String(h.value())));
    }
```
Here's the link for kafka producer:
https://github.com/awnali/spring-boot-kafka-producer

