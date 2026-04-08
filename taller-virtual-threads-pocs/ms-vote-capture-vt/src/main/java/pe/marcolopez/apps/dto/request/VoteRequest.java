package pe.marcolopez.apps.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a request to cast a vote in a poll. This record encapsulates the necessary
 * information required to process a vote, which includes the identifiers of the voter,
 * the poll, and the chosen option, as well as the voter's email address.
 * <p></p>
 * Validation annotations on fields ensure that required data is provided and formatted correctly:
 * - Each field is mandatory (marked with {@code @NotBlank}).
 * - The email field must conform to valid email formatting conventions
 * (enforced via {@code @Email}).
 * <p></p>
 * Fields:
 * - voterId: The unique identifier of the voter submitting the vote.
 * - pollId: The unique identifier of the poll being participated in.
 * - optionId: The unique identifier of the selected poll option.
 * - email: The email address of the voter.
 */
public record VoteRequest(

    @NotBlank(message = "Voter ID es requerido")
    String voterId,

    @NotBlank(message = "Poll ID es requerido")
    String pollId,

    @NotBlank(message = "Option ID es requerido")
    String optionId,

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email no es válido")
    String email
) {
}
