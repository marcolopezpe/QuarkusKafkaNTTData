package pe.marcolopez.apps.service.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.jboss.logging.Logger;
import pe.marcolopez.apps.avro.VoteEvent;
import pe.marcolopez.apps.dto.request.VoteRequest;
import pe.marcolopez.apps.mapper.VoteMapper;
import pe.marcolopez.apps.service.VoteService;
import pe.marcolopez.apps.util.KafkaValues;

/**
 * VoteServiceKafkaImpl is an implementation of the {@link VoteService} interface,
 * responsible for sending voting event data to Kafka for asynchronous processing.
 * This implementation integrates with Kafka using a producer to publish serialized
 * vote events to a specified topic.
 * <p></p>
 * Key Features:
 * - Asynchronous sending of voting events to a Kafka topic.
 * - Mapping of vote requests to Kafka-compatible event objects.
 * - Configuration-driven Kafka integration, leveraging the {@link KafkaValues} interface.
 * <p></p>
 * Dependencies:
 * - {@link KafkaValues}: Provides configuration values required to interact with Kafka,
 * such as the topic name.
 * - {@link Producer}: Kafka producer instance for sending records.
 * - {@link VoteMapper}: Mapper utility for transforming {@link VoteRequest} into
 * {@link VoteEvent}.
 * <p></p>
 * Logging:
 * - Logs success information when a message is successfully sent to Kafka.
 * - Logs error messages in case of a failure during message transmission.
 */
@ApplicationScoped
public class VoteServiceKafkaImpl implements VoteService {

  private static final Logger LOGGER = Logger.getLogger(VoteServiceKafkaImpl.class);

  @Inject
  KafkaValues kafkaValues;

  @Inject
  @Named("producer")
  Producer<String, VoteEvent> producer;

  @Inject
  VoteMapper voteMapper;

  /**
   * Sends a voting event to a Kafka topic asynchronously. This method converts a
   * {@link VoteRequest} into a {@link VoteEvent}, adds necessary headers, and publishes
   * the resulting event to a Kafka topic using a producer. Success or failure of the
   * operation is logged.
   *
   * @param voteRequest the voting request containing information about the voter, poll,
   *                    option, and email. This data is mapped to a {@link VoteEvent}
   *                    before being sent to Kafka.
   */
  @Override
  public void sendVoteAsync(VoteRequest voteRequest) {
    var key = UUID.randomUUID().toString();
    VoteEvent voteEvent = voteMapper.toEvent(voteRequest);

    var headers = new RecordHeaders();
    headers.add("voter-agent", "quarkus-app".getBytes(StandardCharsets.UTF_8));

    var record = new ProducerRecord<>(
        kafkaValues.topic(),
        null,
        key,
        voteEvent,
        headers
    );

    producer.send(record, (metadata, exception) -> {
      if (exception != null) {
        LOGGER.errorf("❌ Error al enviar evento a Kafka: %s", exception.getMessage());
      } else {
        LOGGER.infof("✅ Evento enviado a Kafka topic=%s partition=%d offset=%d",
            metadata.topic(), metadata.partition(), metadata.offset());
      }
    });
  }
}
