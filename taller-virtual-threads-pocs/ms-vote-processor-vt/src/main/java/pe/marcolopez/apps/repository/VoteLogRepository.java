package pe.marcolopez.apps.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;
import pe.marcolopez.apps.model.entity.VoteLogEntity;

/**
 * Repository class for managing data access related to the {@link VoteLogEntity}.
 * This class provides custom database operations for handling vote logs.
 * Implements {@link PanacheRepositoryBase} to leverage simplified data access methods.
 */
@ApplicationScoped
public class VoteLogRepository implements PanacheRepositoryBase<VoteLogEntity, UUID> {

  /**
   * Checks if a vote exists for a given combination of voter ID and poll ID.
   *
   * @param voterId the identifier of the voter whose vote is being checked
   * @param pollId the identifier of the poll for which the vote is being checked
   * @return true if a vote exists for the given voter ID and poll ID; false otherwise
   */
  public Boolean existsByVoterIdAndPollId(String voterId, String pollId) {
    return Optional.ofNullable(
        find("voterId = ?1 and pollId = ?2", voterId, pollId).firstResult()
    ).isPresent();
  }
}
