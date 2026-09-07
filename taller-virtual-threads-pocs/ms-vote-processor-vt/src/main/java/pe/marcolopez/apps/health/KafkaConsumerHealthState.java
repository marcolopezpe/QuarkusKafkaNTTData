package pe.marcolopez.apps.health;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class KafkaConsumerHealthState {

  private final AtomicLong lastPollTimestamp = new AtomicLong(System.currentTimeMillis());

  private volatile boolean connected = true;
  private volatile String lastError = "";

  public void pollSuccess() {
    lastPollTimestamp.set(System.currentTimeMillis());
    connected = true;
    lastError = "";
  }

  public void pollFailure(Exception e) {
    connected = false;
    lastError = e.getMessage();
  }

  public long secondsWithoutPoll() {
    return (System.currentTimeMillis() - lastPollTimestamp.get()) / 1000;
  }

  public boolean isConnected() {
    return connected;
  }

  public String getLastError() {
    return lastError;
  }
}
