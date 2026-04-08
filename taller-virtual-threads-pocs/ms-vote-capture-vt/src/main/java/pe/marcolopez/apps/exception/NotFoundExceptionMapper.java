package pe.marcolopez.apps.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.Map;

/**
 * NotFoundExceptionMapper is an implementation of the ExceptionMapper interface that handles
 * NotFoundException errors and provides a structured response to the client.
 * <p></p>
 * This mapper is specifically triggered when a NotFoundException is thrown. It creates a
 * consistent JSON response to inform the client about the 404 Not Found error.
 * <p></p>
 * The generated response includes the following fields:
 * - timestamp: The exact time when the error occurred.
 * - status: The HTTP status code (404) indicating that the requested resource was not found.
 * - error: A brief description of the error ("Not Found").
 * - message: The detailed message associated with the NotFoundException.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

  /**
   * Converts a NotFoundException into a structured HTTP response with a 404 status code.
   *
   * @param exception the NotFoundException instance that was thrown, containing details
   *                  about the error that occurred
   * @return a Response object with a 404 Not Found status code and a JSON body containing
   *         details such as timestamp, status, error type, and exception message
   */
  @Override
  public Response toResponse(NotFoundException exception) {
    Map<String, Object> body = Map.of(
        "timestamp", Instant.now().toString(),
        "status", 404,
        "error", "Not Found",
        "message", exception.getMessage()
    );

    return Response.status(Response.Status.NOT_FOUND)
        .entity(body)
        .build();
  }
}