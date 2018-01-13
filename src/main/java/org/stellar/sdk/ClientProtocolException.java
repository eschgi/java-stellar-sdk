package org.stellar.sdk;

import org.stellar.sdk.StellarException;

public class ClientProtocolException extends StellarException {
  public ClientProtocolException(String message) {
    super(message);
  }
}
