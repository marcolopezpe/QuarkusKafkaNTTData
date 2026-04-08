package pe.marcolopez.apps.util;

import io.smallrye.config.ConfigMapping;

/**
 * Represents configuration mappings for Kafka-related parameters under the
 * "application.kafka-values" prefix in the configuration file. It provides access
 * to various Kafka settings, including topic management, bootstrap servers,
 * schema registry configuration, and consumer configurations such as client ID,
 * group ID, and polling behavior.
 */
@ConfigMapping(prefix = "application.kafka-values")
public interface KafkaValues {

  String topic();

  String bootstrapServers();

  Schema schema();

  Consumer consumer();

  interface Schema {

    Registry registry();

    interface Registry {

      String url();

      boolean autoRegister();

      boolean useLatestVersion();
    }
  }

  interface Consumer {

    String groupId();

    String clientId();

    int maxPollRecords();

    Avro avro();

    Auto auto();

    Enable enable();

    interface Avro {

      boolean reader();
    }

    interface Auto {

      Offset offset();

      interface Offset {

        String reset();
      }
    }

    interface Enable {

      AutoCommit auto();

      interface AutoCommit {

        boolean commit();
      }
    }
  }
}
