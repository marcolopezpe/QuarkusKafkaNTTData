package pe.marcolopez.apps.util;

import io.smallrye.config.ConfigMapping;

/**
 * Represents configuration mappings for internal application values under the
 * "application.internal-values" prefix in the configuration file.
 * It provides methods to access various configuration properties such as the number
 * of retry attempts and semaphore settings.
 */
@ConfigMapping(prefix = "application.internal-values")
public interface InternalValues {

  int retries();

  Semaphore semaphore();

  interface Semaphore {

    int permits();
  }
}
