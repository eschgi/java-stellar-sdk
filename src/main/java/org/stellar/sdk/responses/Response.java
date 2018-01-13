package org.stellar.sdk.responses;

import okhttp3.Headers;

public abstract class Response {
  protected int rateLimitLimit;
  protected int rateLimitRemaining;
  protected int rateLimitReset;

  public void setHeaders(Headers headers) {
    String rateLimitLimitValue = headers.get("X-Ratelimit-Limit");
    if (rateLimitLimitValue != null) {
      this.rateLimitLimit = Integer.parseInt(rateLimitLimitValue);
    } else {
      this.rateLimitLimit = 0;
    }
    String rateLimitRemainingValue = headers.get("X-Ratelimit-Remaining");
    if (rateLimitRemainingValue != null) {
      this.rateLimitRemaining = Integer.parseInt(rateLimitRemainingValue);
    } else {
      this.rateLimitRemaining = 0;
    }
    String rateLimitResetValue = headers.get("X-Ratelimit-Reset");
    if (rateLimitResetValue != null) {
      this.rateLimitReset = Integer.parseInt(rateLimitResetValue);
    } else {
      this.rateLimitLimit = 0;
    }
  }

  /**
   * Returns X-RateLimit-Limit header from the response.
   * This number represents the he maximum number of requests that the current client can
   * make in one hour.
   * @see <a href="https://www.stellar.org/developers/horizon/learn/rate-limiting.html" target="_blank">Rate Limiting</a>
   */
  public int getRateLimitLimit() {
    return rateLimitLimit;
  }

  /**
   * Returns X-RateLimit-Remaining header from the response.
   * The number of remaining requests for the current window.
   * @see <a href="https://www.stellar.org/developers/horizon/learn/rate-limiting.html" target="_blank">Rate Limiting</a>
   */
  public int getRateLimitRemaining() {
    return rateLimitRemaining;
  }

  /**
   * Returns X-RateLimit-Reset header from the response. Seconds until a new window starts.
   * @see <a href="https://www.stellar.org/developers/horizon/learn/rate-limiting.html" target="_blank">Rate Limiting</a>
   */
  public int getRateLimitReset() {
    return rateLimitReset;
  }
}
