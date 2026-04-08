package pe.marcolopez.apps.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.Map;
import org.jboss.logging.Logger;

/**
 * GlobalExceptionMapper is an implementation of the ExceptionMapper interface to handle
 * unexpected exceptions occurring within the application and provide a consistent structure
 * for error responses.
 * <p></p>
 * This mapper catches any unhandled exceptions and logs the details for debugging purposes. It
 * returns a response with a 500 Internal Server Error status code along with a JSON response body
 * that provides additional information including the error timestamp, status code, error type,
 * and a descriptive message.
 * <p></p>
 * The response body includes:
 * - timestamp: Indicates when the exception occurred.
 * - status: HTTP status code (500).
 * - error: The error type ("Internal Server Error").
 * - message: The exception's descriptive message.
 * <p></p>
 * Logging is performed using the Logger from JBoss logging to provide detailed insights
 * for troubleshooting.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

  private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class);

  /**
   * Builds a structured HTTP response for handling unexpected exceptions and ensures a consistent
   * error response format.
   *
   * @param exception the exception that triggered this mapper, providing details about the
   *                 occurred error
   * @return a Response object with a 500 Internal Server Error status code and a JSON body
   *         containing details such as timestamp, status, error, and exception message
   */
  @Override
  public Response toResponse(Exception exception) {
    LOGGER.errorf("Exception occurred: %s", exception.getMessage(), exception);

    Map<String, Object> body = Map.of(
        "timestamp", Instant.now().toString(),
        "status", 500,
        "error", "Internal Server Error",
        "message", exception.getMessage()
    );

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(body)
        .build();
  }
}