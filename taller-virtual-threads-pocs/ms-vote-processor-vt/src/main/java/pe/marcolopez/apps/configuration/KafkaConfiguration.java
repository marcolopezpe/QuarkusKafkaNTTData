package pe.marcolopez.apps.configuration;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import pe.marcolopez.apps.avro.VoteEvent;
import pe.marcolopez.apps.util.KafkaValues;

/**
 * Configures Kafka Consumer properties and constructs a KafkaConsumer instance using the
 * provided settings.
 * This class integrates configuration values from an injected {@code KafkaValues} instance
 * and applies them to set up a consumer for Kafka communication.
 * <p></p>
 * Responsibilities:
 * 1. Reads and maps Kafka configuration values via the {@code KafkaValues} interface.
 * 2. Sets required Kafka Consumer properties including, but not limited to, server bootstrap,
 * deserialization, schema registry, group id, and polling configurations.
 * 3. Produces a consumer instance as a named CDI producer that can be injected elsewhere using
 * the "consumer" name.
 * <p></p>
 * Annotations:
 * - {@code @Singleton}: Ensures this class is instantiated as a single instance during the
 * application lifecycle.
 * - {@code @Produces}: Marks the consumer creation method to make the generated consumer instance
 * available for injection.
 * - {@code @Named("consumer")}: Associates the produced consumer instance with the
 * name "consumer".
 * <p></p>
 * Methods:
 * {@code createConsumer}:
 * - Prepares and configures a KafkaConsumer with required properties by using information
 * from {@code KafkaValues}.
 * - Returns a fully constructed and configured instance of
 * {@code KafkaConsumer<String, VoteEvent>}.
 * <p></p>
 * {@code getHostname}:
 * - Retrieves the hostname of the current machine. Used to construct client identifier
 * for the Kafka consumer.
 * - Provides "UnknownHost" as a fallback in case the hostname cannot be retrieved.
 * <p></p>
 * Dependencies:
 * - Requires the {@code KafkaValues} interface to supply necessary configuration values such as
 * bootstrap server, schema registry details, and consumer-specific settings
 * (group ID, client ID).
 */
@Singleton
public class KafkaConfiguration {

  @Inject
  KafkaValues kafkaValues;

  /**
   * Creates and configures a Kafka consumer with specified properties for consuming
   * messages from Kafka topics. The consumer is configured using various settings,
   * including bootstrap servers, deserialization classes, schema registry information,
   * and consumer-specific configurations such as group ID and client ID.
   *
   * @return a configured Kafka consumer instance that can consume messages
   *         with keys of type String and values of type VoteEvent from specified
   *         Kafka topics.
   */
  @Produces
  @Named("consumer")
  public Consumer<String, VoteEvent> createConsumer() {
    Properties props = new Properties();

    /*
     * Kafka Bootstrap
     */
    props.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaValues.bootstrapServers());

    /*
     * Deserializers for Keys and Values
     */
    props.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    props.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        KafkaAvroDeserializer.class.getName());

    /*
     * Schema registry
     */
    props.putIfAbsent(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
        kafkaValues.schema().registry().url());
    props.putIfAbsent(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS,
        kafkaValues.schema().registry().autoRegister());
    props.putIfAbsent(AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION,
        kafkaValues.schema().registry().useLatestVersion());

    /*
     * Config for consumer
     */
    props.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG,
        kafkaValues.consumer().groupId());
    props.putIfAbsent(ConsumerConfig.CLIENT_ID_CONFIG,
        kafkaValues.consumer().clientId() + "-" + getHostname());
    props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
        kafkaValues.consumer().auto().offset().reset());
    props.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
        kafkaValues.consumer().enable().auto().commit());
    props.putIfAbsent(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
        kafkaValues.consumer().maxPollRecords());

    /*
     * Avro Reader
     */
    props.putIfAbsent(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,
        kafkaValues.consumer().avro().reader());

    return new KafkaConsumer<>(props);
  }

  /**
   * Retrieves the hostname of the local machine.
   * If the hostname cannot be determined, it returns a default value "UnknownHost".
   *
   * @return the hostname of the local machine or "UnknownHost" if the hostname cannot be resolved
   */
  private String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      return "UnknownHost";
    }
  }
}
