package pe.marcolopez.apps.configuration;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import pe.marcolopez.apps.avro.VoteEvent;
import pe.marcolopez.apps.util.KafkaValues;

/**
 * KafkaConfiguration is a configuration class responsible for producing a Kafka producer
 * instance with the necessary settings for interacting with Kafka brokers and the associated
 * schema registry.
 * <p></p>
 * This class is annotated with {@code @Singleton} to ensure a single shared instance
 * is created and used. It uses dependency injection to get configuration values necessary
 * for setting up the producer.
 */
@Singleton
public class KafkaConfiguration {

  @Inject
  KafkaValues kafkaValues;

  /**
   * Creates and configures an instance of Kafka producer with the necessary properties
   * for sending messages to Kafka topics. The producer is configured to work with
   * schema registry, compression, batching, and acknowledgments.
   *
   * @return a configured Kafka producer instance for producing messages with String keys
   *         and VoteEvent values.
   */
  @Produces
  @Named("producer")
  public Producer<String, VoteEvent> createProducer() {
    Properties props = new Properties();

    /*
     * Kafka Bootstrap
     */
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaValues.bootstrapServers());

    /*
     * Deserializers for Keys and Values
     */
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

    /*
     * Schema registry
     */
    props.put(
        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
        kafkaValues.schema().registry().url()
    );
    props.put(
        AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS,
        kafkaValues.schema().registry().autoRegister()
    );
    props.put(
        AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION,
        kafkaValues.schema().registry().useLatestVersion()
    );

    /*
     * Others configs
     */
    props.put(ProducerConfig.ACKS_CONFIG, "1");
    props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

    return new KafkaProducer<>(props);
  }
}
