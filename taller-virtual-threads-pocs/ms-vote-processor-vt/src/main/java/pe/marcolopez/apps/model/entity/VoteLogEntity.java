package pe.marcolopez.apps.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a log entry for a voting action.
 * This entity corresponds to the "tb_votes_log" table in the database
 * and tracks information related to individual votes cast in a poll.
 * <p></p>
 * Each log entry contains details of the voter, the poll, the selected option,
 * and the timestamp of when the vote was cast.
 * <p></p>
 * The entity uses UUIDs for its identifier and is configured to automatically
 * generate its ID value. The fields are mapped to database columns and enforce
 * non-null constraints where applicable.
 * <p></p>
 * An instance of this class can be created, updated, and persisted using standard
 * JPA or Hibernate operations.
 */
@Entity
@Table(name = "tb_votes_log", schema = "public")
public class VoteLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "voter_id", nullable = false)
  private String voterId;

  @Column(name = "poll_id", nullable = false)
  private String pollId;

  @Column(name = "option_id", nullable = false)
  private String optionId;

  @Column(name = "voted_at", nullable = false)
  private LocalDateTime votedAt;

  public VoteLogEntity() {
  }

  public UUID getId() {
    return id;
  }

  public VoteLogEntity setId(UUID id) {
    this.id = id;
    return this;
  }

  public String getVoterId() {
    return voterId;
  }

  public VoteLogEntity setVoterId(String voterId) {
    this.voterId = voterId;
    return this;
  }

  public String getPollId() {
    return pollId;
  }

  public VoteLogEntity setPollId(String pollId) {
    this.pollId = pollId;
    return this;
  }

  public String getOptionId() {
    return optionId;
  }

  public VoteLogEntity setOptionId(String optionId) {
    this.optionId = optionId;
    return this;
  }

  public LocalDateTime getVotedAt() {
    return votedAt;
  }

  public VoteLogEntity setVotedAt(LocalDateTime votedAt) {
    this.votedAt = votedAt;
    return this;
  }
}
