package org.stellar.sdk.requests;

import okhttp3.HttpUrl;
import org.junit.Test;
import org.stellar.sdk.Server;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class LedgersRequestBuilderTest {
  @Test
  public void testAccounts() {
    Server server = new Server("https://horizon-testnet.stellar.org");
    HttpUrl url = server.ledgers()
            .limit(200)
            .order(RequestBuilder.Order.ASC)
            .buildUrl();
    assertEquals("https://horizon-testnet.stellar.org/ledgers?limit=200&order=asc", url.toString());
  }
}
