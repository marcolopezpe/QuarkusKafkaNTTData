package pe.marcolopez.apps.resource;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import pe.marcolopez.apps.dto.request.VoteRequest;
import pe.marcolopez.apps.service.VoteService;

/**
 * The VoteResource class provides an HTTP endpoint for processing voting requests.
 * The resource is mapped to the path "/api/votes". It handles incoming vote requests
 * and delegates the processing to the {@code VoteService}.
 * <p></p>
 * This class ensures that votes are processed asynchronously while handling any potential
 * errors that might occur during the operation. It uses the {@code Logger} to log error
 * details and returns appropriate HTTP responses based on the outcome.
 * <p></p>
 * Annotations:
 * - {@code @Path("/api/votes")}: Defines the base URI for all endpoints in this resource class.
 * - {@code @POST}: Specifies that the associated method handles HTTP POST requests.
 * - {@code @RunOnVirtualThread}: Denotes that the method is executed using a virtual thread.
 * <p></p>
 * Dependencies:
 * - {@code VoteService}: Handles the underlying processing of votes. This dependency is
 *   injected using the {@code @Inject} annotation.
 */
@Path("/api/votes")
public class VoteResource {

  private static final Logger LOGGER = Logger.getLogger(VoteResource.class);

  @Inject
  VoteService voteService;

  /**
   * Processes a voting request by asynchronously delegating it to the vote service.
   * This method handles HTTP POST requests, ensuring the vote is sent for processing
   * in a non-blocking manner and returns an appropriate HTTP response based on the outcome.
   * If any exception occurs during processing, it logs the error and returns a
   * server error response.
   *
   * @param voteRequest the {@link VoteRequest} containing voter ID, poll ID, option ID,
   *                    and email of the voter to be processed.
   * @return a {@link Response} indicating whether the voting request was accepted (HTTP 202)
   *         or if an error occurred during processing (HTTP 500).
   */
  @POST
  @RunOnVirtualThread
  public Response processVote(VoteRequest voteRequest) {
    try {
      voteService.sendVoteAsync(voteRequest);
      return Response.accepted().build();
    } catch (Exception e) {
      LOGGER.errorf("Error al procesar el voto: %s", e);
      return Response.serverError().entity("Error procesando voto: " + e.getMessage()).build();
    }
  }
}
