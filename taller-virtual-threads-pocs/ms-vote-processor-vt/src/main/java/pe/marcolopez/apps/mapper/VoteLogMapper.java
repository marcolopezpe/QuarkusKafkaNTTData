package pe.marcolopez.apps.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pe.marcolopez.apps.avro.VoteEvent;
import pe.marcolopez.apps.model.entity.VoteLogEntity;

/**
 * Interface for mapping between {@link VoteEvent} and {@link VoteLogEntity}.
 * This is a Jakarta-compatible mapper definition that transforms domain models
 * into persistent entities.
 * <p></p>
 * The mapping specifically processes the date field "votedAt" using a custom
 * transformation method, ensuring correct conversion from a string to
 * {@link LocalDateTime}.
 */
@Mapper(componentModel = "jakarta")
public interface VoteLogMapper {

  /**
   * Maps a {@link VoteEvent} instance to a {@link VoteLogEntity}.
   * The mapping includes transforming the "votedAt" field from String to {@link LocalDateTime}.
   *
   * @param voteEvent the source object containing vote data to be mapped
   * @return a {@link VoteLogEntity} instance with mapped fields from the provided {@link VoteEvent}
   */
  @Mapping(target = "votedAt", expression = "java(mapVotedAt(voteEvent.getVotedAt()))")
  VoteLogEntity toEntity(VoteEvent voteEvent);

  /**
   * Maps the provided string representation of a date-time to a {@link LocalDateTime} instance.
   * If the input string is blank or null, it returns null. Otherwise, it parses the string
   * as an {@link OffsetDateTime} and converts it to {@link LocalDateTime}.
   *
   * @param votedAt the string representation of the date-time in ISO-8601 format
   * @return a {@link LocalDateTime} instance parsed from the input string, or null if the input
   *         is blank or null
   */
  @Named("mapVotedAt")
  default LocalDateTime mapVotedAt(String votedAt) {
    return StringUtils.isBlank(votedAt) ? null : OffsetDateTime.parse(votedAt).toLocalDateTime();
  }
}
