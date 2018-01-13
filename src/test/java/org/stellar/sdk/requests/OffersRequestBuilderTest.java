package org.stellar.sdk.requests;

import okhttp3.HttpUrl;
import org.junit.Test;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class OffersRequestBuilderTest {
  @Test
  public void testForAccount() {
    Server server = new Server("https://horizon-testnet.stellar.org");
    HttpUrl url = server.offers()
            .forAccount(KeyPair.fromAccountId("GBRPYHIL2CI3FNQ4BXLFMNDLFJUNPU2HY3ZMFSHONUCEOASW7QC7OX2H"))
            .limit(200)
            .order(RequestBuilder.Order.DESC)
            .buildUrl();
    assertEquals("https://horizon-testnet.stellar.org/accounts/GBRPYHIL2CI3FNQ4BXLFMNDLFJUNPU2HY3ZMFSHONUCEOASW7QC7OX2H/offers?limit=200&order=desc", url.toString());
  }
}
