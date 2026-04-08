package pe.marcolopez.apps.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;
import org.jboss.logging.Logger;
import pe.marcolopez.apps.avro.VoteEvent;
import pe.marcolopez.apps.mapper.VoteLogMapper;
import pe.marcolopez.apps.repository.VoteLogRepository;
import pe.marcolopez.apps.repository.VoteTotalRepository;
import pe.marcolopez.apps.util.InternalValues;

/**
 * Service class responsible for processing votes within the application.
 * This class performs operations such as managing semaphore-controlled access,
 * persisting vote logs, updating vote totals, and handling transactional
 * operations for voting-related events. It relies on various injected dependencies
 * for database access, configuration, and data mapping.
 * <p></p>
 * The core functionalities include:
 * - Controlling concurrency using semaphore to prevent an excessive load on the
 *   database.
 * - Persisting vote logs to ensure that voting events are tracked accurately.
 * - Managing vote totals and ensuring data integrity using transactional
 *   operations with pessimistic locking.
 * - Preventing duplicate votes by validating voter and poll IDs before persisting.
 */
@ApplicationScoped
public class VoteProcessorService {

  private static final Logger LOGGER = Logger.getLogger(VoteProcessorService.class);

  Semaphore semaphoreToDatabase;

  @Inject
  InternalValues internalValues;

  @Inject
  VoteLogRepository voteLogRepository;

  @Inject
  VoteTotalRepository voteTotalRepository;

  @Inject
  VoteLogMapper voteLogMapper;

  /**
   * Initializes the semaphore used to control the concurrency level for database access.
   * This method is invoked automatically after the dependency injection is complete.
   * <p></p>
   * The semaphore's number of permits is configured based on the value provided
   * by the {@link InternalValues} configuration, specifically through the
   * {@code internalValues.semaphore().permits()} method. This ensures that the
   * level of concurrency is dynamically adjustable through application configuration.
   * <p></p>
   * Usage of this semaphore is critical to prevent database overload by limiting
   * the number of concurrent operations that can be executed against it.
   * <p></p>
   * This method is annotated with {@link PostConstruct} and is executed once upon
   * bean initialization within the application lifecycle.
   */
  @PostConstruct
  void init() {
    semaphoreToDatabase = new Semaphore(internalValues.semaphore().permits());
  }

  /**
   * Processes a given vote event by acquiring semaphore to control database access
   * concurrency and initiating transactional vote operations. Ensures proper release
   * of the semaphore in case of interruption or completion.
   *
   * @param voteEvent the vote event object containing information about the vote to be processed
   */
  public void processVote(VoteEvent voteEvent) {
    try {
      semaphoreToDatabase.acquire();
      initTransactionalVote(voteEvent);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("⛔ Interrupción al adquirir semáforo", e);
    } finally {
      semaphoreToDatabase.release();
    }
  }

  /**
   * Initializes and processes a transactional vote operation. This method checks for
   * duplicate votes, persists the vote log, updates the vote total for the corresponding
   * poll and option, and ensures transactional integrity.
   * <p></p>
   * The method is annotated with {@code @Transactional}, ensuring all operations
   * are executed within a single transaction. If any part of the process fails with a
   * {@link RuntimeException}, the transaction is rolled back.
   *
   * @param vote the {@link VoteEvent} object containing details about the vote, such as voter ID,
   *             poll ID, and option ID
   */
  @Transactional(rollbackOn = RuntimeException.class)
  public void initTransactionalVote(VoteEvent vote) {
    var voterId = vote.getVoterId();
    var pollId = vote.getPollId();
    var optionId = vote.getOptionId();

    if (voteLogRepository.existsByVoterIdAndPollId(voterId, pollId)) {
      LOGGER.warnf("⚠️ Voto duplicado para voterId=%s and pollId=%s. Omitiendo...",
          voterId, pollId);
    } else {
      voteLogRepository.persist(voteLogMapper.toEntity(vote));

      var fetchVoteTotal = voteTotalRepository.findAndLockByPollIdAndOptionId(pollId, optionId);
      if (fetchVoteTotal == null) {
        throw new RuntimeException(String.format("PollId=%s y OptionId=%s no encontrado",
            pollId, optionId));
      }

      fetchVoteTotal.setTotal(fetchVoteTotal.getTotal() + 1);
      fetchVoteTotal.setUpdatedAt(LocalDateTime.now());
      // voteTotalRepository.persist(fetchVoteTotal);

      LOGGER.infof("✅ Voto procesado correctamente para voterId=%s, pollId=%s",
          voterId, pollId);
    }
  }
}
