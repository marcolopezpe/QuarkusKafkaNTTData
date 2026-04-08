package pe.marcolopez.apps.util;

import io.smallrye.config.ConfigMapping;

/**
 * Represents the configuration mapping for Kafka values as specified under the
 * "application.kafka-values" prefix. This configuration provides necessary Kafka
 * settings such as topic name, bootstrap servers, and schema registry information.
 * <p></p>
 * This interface is typically used to inject Kafka-related configuration values in
 * an application that interacts with Kafka. The structure includes nested interfaces
 * to define schema and registry configuration details.
 */
@ConfigMapping(prefix = "application.kafka-values")
public interface KafkaValues {

  String topic();

  String bootstrapServers();

  Schema schema();

  interface Schema {

    Registry registry();

    interface Registry {

      String url();

      boolean autoRegister();

      boolean useLatestVersion();
    }
  }
}
