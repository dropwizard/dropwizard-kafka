# dropwizard-kafka
[![Build](https://github.com/dropwizard/dropwizard-kafka/workflows/Build/badge.svg)](https://github.com/dropwizard/dropwizard-kafka/actions?query=workflow%3ABuild)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dropwizard_dropwizard-kafka&metric=alert_status)](https://sonarcloud.io/dashboard?id=dropwizard_dropwizard-kafka)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.dropwizard.modules/dropwizard-kafka/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.dropwizard.modules/dropwizard-kafka/)

Provides easy integration for Dropwizard applications with the Apache Kafka client.

This bundle comes with out-of-the-box support for:
* YAML Configuration integration
* Producer and Consumer lifecycle management
* Producer and Cluster connection health checks
* Metrics integration for the Kafka client
* An easier way to create/configure Kafka consumers/producers than is offered by the base Kafka client
* Distributed tracing integration, using the [Brave Kafka client instrumentation library](https://github.com/openzipkin/brave/tree/master/instrumentation/kafka-clients).

For more information on Kafka, take a look at the official documentation here: http://kafka.apache.org/documentation/

## Dropwizard Version Support Matrix
| dropwizard-kafka | Dropwizard v1.3.x  | Dropwizard v2.0.x  | Dropwizard v2.1.x  | Dropwizard v3.0.x  | Dropwizard v4.0.x  |
|------------------|--------------------|--------------------|--------------------|--------------------|--------------------|
| v1.3.x           | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x:                | :x:                |
| v1.4.x           | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x:                | :x:                |
| v1.5.x           | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x:                | :x:                |
| v1.6.x           | :x:                | :white_check_mark: | :white_check_mark: | :x:                | :x:                |
| v1.7.x           | :x:                | :white_check_mark: | :white_check_mark: | :x:                | :x:                |
| v1.8.x           | :x:                | :x:                | :white_check_mark: | :x:                | :x:                |
| v3.0.x           | :x:                | :x:                | :x:                | :white_check_mark: | :x:                |
| v4.0.x           | :x:                | :x:                | :x:                | :x:                | :white_check_mark: |

## Usage
Add dependency on library.

Maven:
```xml
<dependency>
  <groupId>io.dropwizard.modules</groupId>
  <artifactId>dropwizard-kafka</artifactId>
  <version>1.7.0</version>
</dependency>
```

Gradle:
```groovy
compile "io.dropwizard.modules:dropwizard-kafka:$dropwizardVersion"
```

### Basic Kafka Producer
In your Dropwizard `Configuration` class, configure a `KafkaProducerFactory`:
```java
@Valid
@NotNull
@JsonProperty("producer")
private KafkaProducerFactory<String, String> kafkaProducerFactory;
```

Then, in your `Application` class, you'll want to do something similar to the following:
```java
private final KafkaProducerBundle<String, String, ExampleConfiguration> kafkaProducer = new KafkaProducerBundle<String, String, ExampleConfiguration>() {
    @Override
    public KafkaProducerFactory<String, String> getKafkaProducerFactory(ExampleConfiguration configuration) {
        return configuration.getKafkaProducerFactory();
    }
};

@Override
public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
    bootstrap.addBundle(kafkaProducer);
}

@Override
public void run(ExampleConfiguration config, Environment environment) {
    final PersonEventProducer personEventProducer = new PersonEventProducer(kafkaProducer.getProducer());
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
  keySerializer:
    type: string
  valueSerializer:
    type: string
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
private KafkaConsumerFactory<String, String> kafkaConsumerFactory;
```

Then, in your `Application` class, you'll want to do something similar to the following:
```java
private final KafkaConsumerBundle<String, String, ExampleConfiguration> kafkaConsumer = new KafkaConsumerBundle<String, String, ExampleConfiguration>() {
    @Override
    public KafkaConsumerFactory<String, String> getKafkaConsumerFactory(ExampleConfiguration configuration) {
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
  keyDeserializer:
    type: string
  valueDeserializer:
    type: string
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

## Adding support for additional serializers and/or deserializers
In order to support additional serializers or deserializers, you'll need to create a new factory:
```java
@JsonTypeName("my-serializer")
public class MySerializerFactory extends SerializerFactory {

    @NotNull
    @JsonProperty
    private String someConfig;

    public String getSomeConfig() {
        return someConfig;
    }

    public void setSomeConfig(final String someConfig) {
        this.someConfig = someConfig;
    }


    @Override
    public Class<? extends Serializer> getSerializerClass() {
        return MySerializer.class;
    }
}
```

Then you will need to add the following files to your `src/main/resources/META-INF/services` directory in order to support Jackson
polymorphic serialization:

File named `io.dropwizard.jackson.Discoverable`:

```
io.dropwizard.kafka.serializer.SerializerFactory
```

File named `io.dropwizard.kafka.serializer.SerializerFactory`:

```
package.name.for.your.MySerializerFactory
```
