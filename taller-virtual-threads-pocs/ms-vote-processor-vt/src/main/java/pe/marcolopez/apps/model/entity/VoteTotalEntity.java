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
 * Entity class representing the total votes for a specific poll option.
 * This entity corresponds to the "tb_votes_total" table in the database.
 * <p></p>
 * The class is designed to store aggregated vote counts for individual options in a poll.
 * It includes identifiers for the poll and option, along with the total number of votes
 * recorded. Additionally, it tracks creation and update timestamps for audit purposes.
 * <p></p>
 * The `id` field serves as the primary key and is automatically generated as a UUID.
 * Other fields are mapped to database columns and include nullability constraints to
 * ensure data integrity.
 * <p></p>
 * Instances of this class can be managed using JPA or Hibernate for standard CRUD
 * operations. The provided setters follow a fluent API pattern for convenient chaining
 * of method calls.
 */
@Entity
@Table(name = "tb_votes_total", schema = "public")
public class VoteTotalEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "poll_id", nullable = false)
  private String pollId;

  @Column(name = "option_id", nullable = false)
  private String optionId;

  @Column(name = "total", nullable = false)
  private Integer total;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public VoteTotalEntity() {
  }

  public UUID getId() {
    return id;
  }

  public VoteTotalEntity setId(UUID id) {
    this.id = id;
    return this;
  }

  public String getPollId() {
    return pollId;
  }

  public VoteTotalEntity setPollId(String pollId) {
    this.pollId = pollId;
    return this;
  }

  public String getOptionId() {
    return optionId;
  }

  public VoteTotalEntity setOptionId(String optionId) {
    this.optionId = optionId;
    return this;
  }

  public Integer getTotal() {
    return total;
  }

  public VoteTotalEntity setTotal(Integer total) {
    this.total = total;
    return this;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public VoteTotalEntity setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public VoteTotalEntity setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }
}
