package pe.marcolopez.apps.consumer;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.logging.Logger;
import pe.marcolopez.apps.avro.VoteEvent;
import pe.marcolopez.apps.service.VoteProcessorService;
import pe.marcolopez.apps.util.InternalValues;
import pe.marcolopez.apps.util.KafkaValues;

/**
 * The VoteKafkaServiceConsumer is responsible for consuming messages from a Kafka topic
 * and processing vote events. It initializes the Kafka consumer on application startup
 * and handles shutdown processes properly to ensure thread termination and resource release.
 * The class also includes mechanisms for retrying the processing of messages in case of
 * failures, ensuring resilience in the face of transient issues.
 * <p></p>
 * Features:
 * - Subscribes to a specific Kafka topic based on the configured application settings.
 * - Polls Kafka messages and submits them for further processing using a virtual thread executor.
 * - Performs retries for processing messages and logs failures if the maximum retry attempts
 *   are exceeded.
 * - Gracefully handles application shutdown by stopping consumer polling and releasing resources.
 * <p></p>
 * Dependencies:
 * - `KafkaValues`: Provides configuration for Kafka, including the topic name and other
 *   consumer-related settings.
 * - `InternalValues`: Provides internal configuration, including the number of retries allowed.
 * - `Consumer`: A Kafka consumer instance for reading messages.
 * - `VoteProcessorService`: A service responsible for processing individual vote events.
 * - `ExecutorService`: A service that supports virtual threads for task execution.
 */
@ApplicationScoped
public class VoteKafkaServiceConsumer {

  private static final Logger LOGGER = Logger.getLogger(VoteKafkaServiceConsumer.class);
  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  @Inject
  KafkaValues kafkaValues;

  @Inject
  InternalValues internalValues;

  @Inject
  @Named("consumer")
  Consumer<String, VoteEvent> consumer;

  @Inject
  VoteProcessorService voteProcessorService;

  volatile boolean running = true;

  /**
   * Initializes the Kafka consumer by subscribing to the specified topic and starts
   * an asynchronous thread to continuously poll for records. The method processes
   * consumed records with a retry mechanism and commits offsets asynchronously.
   *
   * @param event the startup event that triggers the initialization process
   */
  public void initialize(@Observes StartupEvent event) {
    consumer.subscribe(Collections.singletonList(kafkaValues.topic()));
    executor.submit(() -> {
      while (running) {
        try {
          var records = consumer.poll(Duration.ofMillis(200));
          if (!records.isEmpty()) {
            records.forEach(
                record ->
                    executor.submit(() ->
                        processRecordWithRetry(record, internalValues.retries()))
            );
            consumer.commitAsync();
          }
        } catch (Exception ex) {
          LOGGER.error("❌ Error en el polling de Kafka", ex);
        }
      }
    });
  }

  /**
   * Terminates the Kafka consumer operations upon application shutdown.
   * This method stops the execution of the consumer thread, shuts down the
   * executor service, and signals to cease activity, ensuring a graceful shutdown.
   *
   * @param event the shutdown event that triggers the termination process
   */
  public void terminate(@Observes ShutdownEvent event) {
    running = false;
    executor.shutdown();
    consumer.wakeup();
  }

  /**
   * Processes a Kafka {@code ConsumerRecord} containing a vote event, retrying the processing
   * operation up to a specified number of attempts in case of an exception. Each processing
   * attempt is logged for monitoring purposes, and failure after the allowed retries is
   * also logged.
   *
   * @param record the {@code ConsumerRecord} containing the vote event to be processed
   * @param retries the maximum number of retry attempts allowed for processing the record
   */
  private void processRecordWithRetry(ConsumerRecord<String, VoteEvent> record, int retries) {
    int attempt = 0;
    boolean success = false;

    while (attempt < retries && !success) {
      try {
        voteProcessorService.processVote(record.value());
        success = true;
      } catch (Exception e) {
        attempt++;
        LOGGER.warnf("⚠️ Intento %d fallido al procesar voto key=%s: %s",
            attempt, record.key(), e.getMessage());
        if (attempt == retries) {
          success = true;
          LOGGER.errorf("❌ Fallo al procesar voto key=%s luego de %d intentos. Se omite.",
              record.key(), retries);
        }
      }
    }
  }
}
