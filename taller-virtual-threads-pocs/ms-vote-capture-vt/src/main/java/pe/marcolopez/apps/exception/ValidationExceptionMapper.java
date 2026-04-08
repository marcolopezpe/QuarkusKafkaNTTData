package pe.marcolopez.apps.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ValidationExceptionMapper is an implementation of the ExceptionMapper interface
 * that handles ConstraintViolationException instances and provides a structured
 * response for validation errors.
 * <p></p>
 * This mapper is triggered specifically when a ConstraintViolationException occurs,
 * usually as a result of validation errors on input fields. It constructs a consistent
 * JSON response aimed at clearly communicating the nature of the validation issues to clients.
 * <p></p>
 * The generated response includes:
 * - timestamp: The exact time when the error occurred.
 * - status: The HTTP status code (400) indicating a Bad Request.
 * - error: A brief description of the error ("Bad Request").
 * - message: A general message describing the occurrence of validation errors.
 * - violations: A list of validation errors, where each entry specifies:
 *   - field: The name of the field that failed validation.
 *   - message: The specific validation message associated with the field.
 * <p></p>
 * The utility method `extractFieldName` is used to extract the field name from the
 * property path of the validation error, ensuring clarity for nested or complex property paths.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

  /**
   * Converts a ConstraintViolationException into a structured HTTP response with a 400 status code.
   * <p></p>
   * The generated response includes:
   * - timestamp: Indicates when the exception occurred.
   * - status: The HTTP status code (400) indicating a Bad Request.
   * - error: A brief description of the error ("Bad Request").
   * - message: A general message describing the encountered validation error.
   * - violations: A list of validation errors, where each entry contains:
   *   - field: The name of the field that caused the validation error.
   *   - message: A specific error message related to the field.
   *
   * @param exception the ConstraintViolationException instance that was thrown, containing details
   *                  about validation errors on input fields
   * @return a Response object with a 400 Bad Request status code and a JSON body containing details
   *         such as timestamp, status, error, message, and validation violations
   */
  @Override
  public Response toResponse(ConstraintViolationException exception) {
    List<Map<String, String>> violations = exception.getConstraintViolations().stream()
        .map(violation -> Map.of(
            "field", extractFieldName(violation),
            "message", violation.getMessage()
        ))
        .collect(Collectors.toList());

    Map<String, Object> responseBody = Map.of(
        "timestamp", Instant.now().toString(),
        "status", 400,
        "error", "Bad Request",
        "message", "Error de validación de campos",
        "violations", violations
    );

    return Response.status(Response.Status.BAD_REQUEST)
        .entity(responseBody)
        .build();
  }

  /**
   * Extracts the name of the field from the property path of a ConstraintViolation.
   * If the property path is nested (e.g., "create.arg0.name"), the last segment of
   * the path is extracted.
   *
   * @param violation the ConstraintViolation object containing the property path to process
   * @return the extracted field name as a string
   */
  private String extractFieldName(ConstraintViolation<?> violation) {
    String path = violation.getPropertyPath().toString();
    return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
  }
}
