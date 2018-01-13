package org.stellar.sdk.requests;

import com.google.gson.reflect.TypeToken;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.effects.EffectResponse;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builds requests connected to effects.
 */
public class EffectsRequestBuilder extends RequestBuilder {
  public EffectsRequestBuilder(OkHttpClient httpClient, HttpUrl serverUrl) {
    super(httpClient, serverUrl, "effects");
  }

  /**
   * Builds request to <code>GET /accounts/{account}/effects</code>
   * @see <a href="https://www.stellar.org/developers/horizon/reference/effects-for-account.html">Effects for Account</a>
   * @param account Account for which to get effects
   */
  public EffectsRequestBuilder forAccount(KeyPair account) {
    account = checkNotNull(account, "account cannot be null");
    this.setPathSegments("accounts", account.getAccountId(), "effects");
    return this;
  }

  /**
   * Builds request to <code>GET /ledgers/{ledgerSeq}/effects</code>
   * @see <a href="https://www.stellar.org/developers/horizon/reference/effects-for-ledger.html">Effects for Ledger</a>
   * @param ledgerSeq Ledger for which to get effects
   */
  public EffectsRequestBuilder forLedger(long ledgerSeq) {
    this.setPathSegments("ledgers", String.valueOf(ledgerSeq), "effects");
    return this;
  }

  /**
   * Builds request to <code>GET /transactions/{transactionId}/effects</code>
   * @see <a href="https://www.stellar.org/developers/horizon/reference/effects-for-transaction.html">Effect for Transaction</a>
   * @param transactionId Transaction ID for which to get effects
   */
  public EffectsRequestBuilder forTransaction(String transactionId) {
    transactionId = checkNotNull(transactionId, "transactionId cannot be null");
    this.setPathSegments("transactions", transactionId, "effects");
    return this;
  }

  /**
   * Builds request to <code>GET /operation/{operationId}/effects</code>
   * @see <a href="https://www.stellar.org/developers/horizon/reference/effects-for-operation.html">Effect for Operation</a>
   * @param operationId Operation ID for which to get effects
   */
  public EffectsRequestBuilder forOperation(long operationId) {
    this.setPathSegments("operations", String.valueOf(operationId), "effects");
    return this;
  }

  /**
   * Allows to stream SSE events from horizon.
   * Certain endpoints in Horizon can be called in streaming mode using Server-Sent Events.
   * This mode will keep the connection to horizon open and horizon will continue to return
   * responses as ledgers close.
   * @see <a href="http://www.w3.org/TR/eventsource/" target="_blank">Server-Sent Events</a>
   * @see <a href="https://www.stellar.org/developers/horizon/learn/responses.html" target="_blank">Response Format documentation</a>
   * @param listener {@link EventListener} implementation with {@link EffectResponse} type
   * @return EventSource object, so you can <code>close()</code> connection when not needed anymore
   */
  /*
  public EventSource stream(final EventListener<EffectResponse> listener) {
    Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
    WebTarget target = client.target(this.buildUri());
    EventSource eventSource = new EventSource(target) {
      @Override
      public void onEvent(InboundEvent inboundEvent) {
        String data = inboundEvent.readData(String.class);
        if (data.equals("\"hello\"")) {
          return;
        }
        EffectResponse effect = GsonSingleton.getInstance().fromJson(data, EffectResponse.class);
        listener.onEvent(effect);
      }
    };
    return eventSource;
  }*/

  /**
   * Build and execute request.
   * @return {@link Page} of {@link EffectResponse}
   * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
   * @throws IOException
   */
  public Page<EffectResponse> execute() throws IOException, TooManyRequestsException {
    return execute(buildUrl());
  }

  /**
   * Requests specific <code>uri</code> and returns {@link Page} of {@link EffectResponse}.
   * This method is helpful for getting the next set of results.
   * @return {@link Page} of {@link EffectResponse}
   * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
   * @throws IOException
   */
  public Page<EffectResponse> execute(HttpUrl url) throws IOException, TooManyRequestsException {
    TypeToken typeToken = new TypeToken<Page<EffectResponse>>() {};
    return get(url, typeToken.getType());
  }

  @Override
  public EffectsRequestBuilder cursor(String token) {
    super.cursor(token);
    return this;
  }

  @Override
  public EffectsRequestBuilder limit(int number) {
    super.limit(number);
    return this;
  }

  @Override
  public EffectsRequestBuilder order(Order direction) {
    super.order(direction);
    return this;
  }
}
