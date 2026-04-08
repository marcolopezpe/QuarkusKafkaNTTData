package pe.marcolopez.apps.service;

import java.util.concurrent.ExecutionException;
import pe.marcolopez.apps.dto.request.VoteRequest;

/**
 * VoteService is an interface that defines a contract for handling voting operations.
 * It includes functionality to asynchronously send voting event data for processing.
 * <p></p>
 * Methods:
 * - {@link #sendVoteAsync}: Sends a vote request asynchronously and handles potential
 *   exceptions during the execution process.
 */
public interface VoteService {

  /**
   * Sends a voting request asynchronously for processing.
   * This method allows for non-blocking execution, and the vote processing
   * occurs in the background. Any exceptions that might occur during the
   * execution or while waiting for the asynchronous operation are propagated.
   *
   * @param voteRequest the {@link VoteRequest} containing the details of the
   *                     vote to be processed, such as voter ID, poll ID,
   *                     option ID, and the voter's email.
   * @throws ExecutionException   if an error occurs while attempting to process
   *                              the vote asynchronously.
   * @throws InterruptedException if the current thread is interrupted while
   *                              waiting for the asynchronous operation to complete.
   */
  void sendVoteAsync(VoteRequest voteRequest) throws ExecutionException, InterruptedException;
}
