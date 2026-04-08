package pe.marcolopez.apps.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.util.UUID;
import pe.marcolopez.apps.model.entity.VoteTotalEntity;

/**
 * Repository class for managing data access related to the {@link VoteTotalEntity}.
 * This class provides custom database operations for handling vote totals.
 * Implements {@link PanacheRepositoryBase} to leverage simplified data access methods.
 */
@ApplicationScoped
public class VoteTotalRepository implements PanacheRepositoryBase<VoteTotalEntity, UUID> {

  /**
   * Retrieves and locks a {@link VoteTotalEntity} record by the specified poll ID and option ID.
   * The method uses a pessimistic write lock to ensure safe concurrent updates to the
   * database record.
   *
   * @param pollId the identifier of the poll to search for
   * @param optionId the identifier of the option to search for
   * @return the {@link VoteTotalEntity} corresponding to the given poll ID and option ID,
   *         or {@code null} if no matching record is found
   */
  public VoteTotalEntity findAndLockByPollIdAndOptionId(String pollId, String optionId) {
    return find("pollId = ?1 and optionId = ?2", pollId, optionId)
        .withLock(LockModeType.PESSIMISTIC_WRITE)
        .firstResult();
  }
}
