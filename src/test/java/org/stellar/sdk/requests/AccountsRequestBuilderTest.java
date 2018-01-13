package org.stellar.sdk.requests;

import okhttp3.HttpUrl;
import org.junit.Test;
import org.stellar.sdk.Server;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class AccountsRequestBuilderTest {
  @Test
  public void testAccounts() {
    Server server = new Server("https://horizon-testnet.stellar.org");
    HttpUrl url = server.accounts()
            .cursor("13537736921089")
            .limit(200)
            .order(RequestBuilder.Order.ASC)
            .buildUrl();
    assertEquals("https://horizon-testnet.stellar.org/accounts?cursor=13537736921089&limit=200&order=asc", url.toString());
  }
}
