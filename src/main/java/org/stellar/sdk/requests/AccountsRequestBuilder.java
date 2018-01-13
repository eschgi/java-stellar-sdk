package org.stellar.sdk.requests;

import com.google.gson.reflect.TypeToken;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.Page;

import java.io.IOException;

/**
 * Builds requests connected to accounts.
 */
public class AccountsRequestBuilder extends RequestBuilder {
  public AccountsRequestBuilder(OkHttpClient httpClient, HttpUrl serverUrl) {
    super(httpClient, serverUrl, "accounts");
  }

  /**
   * Requests specific <code>uri</code> and returns {@link AccountResponse}.
   * This method is helpful for getting the links.
   * @throws IOException
   */
  public AccountResponse account(HttpUrl url) throws IOException {
    return get(url, AccountResponse.class);
  }

  /**
   * Requests <code>GET /accounts/{account}</code>
   * @see <a href="https://www.stellar.org/developers/horizon/reference/accounts-single.html">Account Details</a>
   * @param account Account to fetch
   * @throws IOException
   */
  public AccountResponse account(KeyPair account) throws IOException {
    this.setSegments("accounts", account.getAccountId());
    return this.account(this.buildUrl());
  }

  /**
   * Allows to stream SSE events from horizon.
   * Certain endpoints in Horizon can be called in streaming mode using Server-Sent Events.
   * This mode will keep the connection to horizon open and horizon will continue to return
   * responses as ledgers close.
   * @see <a href="http://www.w3.org/TR/eventsource/" target="_blank">Server-Sent Events</a>
   * @see <a href="https://www.stellar.org/developers/horizon/learn/responses.html" target="_blank">Response Format documentation</a>
   * @param listener {@link EventListener} implementation with {@link AccountResponse} type
   * @return EventSource object, so you can <code>close()</code> connection when not needed anymore
   */
  /*
  public EventSource stream(final EventListener<AccountResponse> listener) {
    Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
    WebTarget target = client.target(this.buildUri());
    EventSource eventSource = new EventSource(target) {
      @Override
      public void onEvent(InboundEvent inboundEvent) {
        String data = inboundEvent.readData(String.class);
        if (data.equals("\"hello\"")) {
          return;
        }
        AccountResponse account = GsonSingleton.getInstance().fromJson(data, AccountResponse.class);
        listener.onEvent(account);
      }
    };
    return eventSource;
  }*/

  /**
   * Build and execute request. <strong>Warning!</strong> {@link AccountResponse}s in {@link Page} will contain only <code>keypair</code> field.
   * @return {@link Page} of {@link AccountResponse}
   * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
   * @throws IOException
   */
  public Page<AccountResponse> execute() throws IOException, TooManyRequestsException {
    return execute(buildUrl());
  }

  /**
   * Requests specific <code>uri</code> and returns {@link Page} of {@link AccountResponse}.
   * This method is helpful for getting the next set of results.
   * @return {@link Page} of {@link AccountResponse}
   * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
   * @throws IOException
   */
  public Page<AccountResponse> execute(HttpUrl url) throws IOException, TooManyRequestsException {
    TypeToken typeToken = new TypeToken<Page<AccountResponse>>() {};
    return get(url, typeToken.getType());
  }

  @Override
  public AccountsRequestBuilder cursor(String token) {
    super.cursor(token);
    return this;
  }

  @Override
  public AccountsRequestBuilder limit(int number) {
    super.limit(number);
    return this;
  }

  @Override
  public AccountsRequestBuilder order(Order direction) {
    super.order(direction);
    return this;
  }
}
