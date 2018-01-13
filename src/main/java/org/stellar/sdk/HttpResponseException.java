package org.stellar.sdk;

public class HttpResponseException extends StellarException {
  private int statusCode;

  public HttpResponseException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  /**
   * Returns the http status code
   */
  public int getStatusCode() {
    return statusCode;
  }
}
