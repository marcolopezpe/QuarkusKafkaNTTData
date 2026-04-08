package pe.marcolopez.apps.mapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pe.marcolopez.apps.avro.VoteEvent;
import pe.marcolopez.apps.dto.request.VoteRequest;

/**
 * Mapper interface for converting {@code VoteRequest} objects to {@code VoteEvent} objects.
 * This mapper uses MapStruct for generating implementation code and applies Jakarta CDI
 * for dependency injection.
 * <p></p>
 * The conversion includes:
 * - Mapping properties from the {@code VoteRequest} to the corresponding fields
 * in {@code VoteEvent}.
 * - Automatically setting the current timestamp in UTC as the "votedAt" property
 * of the {@code VoteEvent}.
 * <p></p>
 * Methods defined in this interface are processed by MapStruct to generate their
 * implementations at runtime.
 */
@Mapper(componentModel = "jakarta")
public interface VoteMapper {

  /**
   * Converts a {@code VoteRequest} object to a {@code VoteEvent} object.
   * This method maps the properties of the {@code VoteRequest} to the corresponding fields
   * in the {@code VoteEvent}. Additionally, it sets the current timestamp in UTC as the
   * "votedAt" property of the resulting {@code VoteEvent}.
   *
   * @param voteRequest the {@code VoteRequest} object to be converted
   * @return a new {@code VoteEvent} object with the mapped properties and current UTC timestamp
   */
  @Mapping(target = "votedAt", expression = "java(mapNowToUtcString())")
  VoteEvent toEvent(VoteRequest voteRequest);

  /**
   * Generates the current date and time in UTC as a string.
   * This method utilizes {@code OffsetDateTime} with a {@code ZoneOffset.UTC}
   * to provide a standardized timestamp in ISO-8601 format.
   *
   * @return a string representation of the current UTC timestamp in ISO-8601 format
   */
  @Named("mapNowToUtcString")
  default String mapNowToUtcString() {
    return OffsetDateTime.now(ZoneOffset.UTC).toString();
  }
}
