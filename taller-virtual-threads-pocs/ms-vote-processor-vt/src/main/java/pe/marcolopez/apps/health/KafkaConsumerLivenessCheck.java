package pe.marcolopez.apps.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class KafkaConsumerLivenessCheck implements HealthCheck {

  @Inject
  KafkaConsumerHealthState state;


}
