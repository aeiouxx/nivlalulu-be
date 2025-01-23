package com.nivlalulu.nnpro.security.service;

import java.time.Duration;

public interface IRateLimiter {

   /**
   * Check if the request is allowed
   * @param key
   * @param limit
   * @param window
   * @return
   */
   boolean isAllowed(String key, int limit, Duration window);
}
