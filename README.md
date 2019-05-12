# dropwizard-kafka

Provides easy integration for Dropwizard applications with the Apache Kafka client. 

This bundle comes with out-of-the-box support for:
* YAML Configuration integration  
* Producer and Consumer lifecycle management
* Producer and Consumer health checks
* Metrics integration for the Kafka client
* An easier way to create/configure Kafka consumers/producers than is offered by the base Kafka client
* Distributed tracing integration, using the [Brave Kafka client instrumentation library](https://github.com/openzipkin/brave/tree/master/instrumentation/kafka-clients).

For more information on Kafka, take a look at the official documentation here: http://kafka.apache.org/documentation/

## Usage
Add dependency on library.

Maven:
```xml
<dependency>
  <groupId>io.dropwizard</groupId>
  <artifactId>dropwizard-kafka</artifactId>
  <version>${dropwizard.version}</version>
</dependency>
```

Gradle:
```groovy
compile "io.dropwizard:dropwizard-kafka:$dropwizardVersion"
```

### Basic Kafka Producer
In your Dropwizard `Configuration` class, configure a `KafkaProducerFactory`:
```java
@Valid
@NotNull
@JsonProperty("producer")
private KafkaProducerFactory kafkaProducerFactory;
```

Then, in your `Application` class, you'll want to do something similar to the following:
```java
private final KafkaProducerBundle<ExampleConfiguration> kafkaProducer = new KafkaProducerBundle<ExampleConfiguration>() {
    @Override
    public KafkaProducerFactory getKafkaProducerFactory(ExampleConfiguration configuration) {
        return configuration.getKafkaProducerFactory();
    }
};

@Override
public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
    bootstrap.addBundle(kafkaProducer);
}

@Override
public void run(ExampleConfiguration config, Environment environment) {
    final PersonEventProducer personEventProducer = new PersonEventProducer(kafkaConsumer.getProducer());
    environment.jersey().register(new PersonEventResource(personEventProducer));
}
```

Configure your factory in your `config.yml` file:

```yaml
producer:
  type: basic
  bootstrapServers:
    - 127.0.0.1:9092
    - 127.0.0.1:9093
    - 127.0.0.1:9094
  name: producerNameToBeUsedInMetrics
  keySerializerClass: org.apache.kafka.common.serialization.StringSerializer
  valueSerializerClass: org.apache.kafka.common.serialization.StringDeserializer
  acks: all
  retries: 2147483647 # int max value
  maxInFlightRequestsPerConnection: 1
  maxPollBlockTime: 10s
  security:
    securityProtocol: sasl_ssl
    sslProtocol: TLSv1.2
    saslMechanism: PLAIN
    saslJaas: "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"<username>\" password=\"<password>\";"
```

### Basic Kafka Consumer
In your Dropwizard `Configuration` class, configure a `KafkaConsumerFactory`:

```java
@Valid
@NotNull
@JsonProperty("consumer")
private KafkaConsumerFactory kafkaConsumerFactory;
```

Then, in your `Application` class, you'll want to do something similar to the following:
```java
private final KafkaConsumerBundle<ExampleConfiguration> kafkaConsumer = new KafkaConsumerBundle<ExampleConfiguration>() {
    @Override
    public KafkaConsumerFactory getKafkaConsumerFactory(ExampleConfiguration configuration) {
        return configuration.getKafkaConsumerFactory();
    }
};

@Override
public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
    bootstrap.addBundle(kafkaConsumer);
}

@Override
public void run(ExampleConfiguration config, Environment environment) {
    final PersonEventConsumer personEventConsumer = new PersonEventConsumer(kafkaConsumer.getConsumer());
    personEventConsumer.startConsuming();
}
```

Configure your factory in your `config.yml` file:

```yaml
consumer: 
  type: basic
  bootstrapServers:
    - 127.0.0.1:9092
    - 127.0.0.1:9093
    - 127.0.0.1:9094
  consumerGroupId: consumer1
  name: consumerNameToBeUsedInMetrics  
  keyDeserializerClass: org.apache.kafka.common.serialization.StringDeserializer
  valueDeserializerClass: org.apache.kafka.common.serialization.StringDeserializer
  security:
    securityProtocol: sasl_ssl
    sslProtocol: TLSv1.2
    saslMechanism: PLAIN
    saslJaas: "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"<username>\" password=\"<password>\";"
```

## Using an older version of the Kafka Client
This library *should* remain backwards compatible, such that you can override the version of the kafka client that this library includes. If this becomes no longer possible, we will need to create separate branches for differing major versions of the Kafka client.

For example, say you would like to use version `1.1.1` of the Kafka client. One option would be to explicitly define a dependency on version `1.1.1` of `kafka-clients` before you declare a dependency on `dropwizard-kafka`.

```xml
<dependencies>
  <dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>1.1.1</version>
  </dependency>
  <dependency>
    <groupId>io.dropwizard</groupId>
    <artifactId>dropwizard-kafka</artifactId>
    <version>${dropwizard.version}</version>
  </dependency>
</dependencies>
``` 
