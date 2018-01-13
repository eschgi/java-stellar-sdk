package org.stellar.sdk.requests;

import com.google.gson.reflect.TypeToken;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.TransactionResponse;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builds requests connected to transactions.
 */
public class TransactionsRequestBuilder extends RequestBuilder {
  public TransactionsRequestBuilder(OkHttpClient httpClient, HttpUrl serverUrl) {
    super(httpClient, serverUrl, "transactions");
  }

  /**
   * Requests specific <code>uri</code> and returns {@link TransactionResponse}.
   * This method is helpful for getting the links.
   * @throws IOException
   */
  public TransactionResponse transaction(HttpUrl url) throws IOException {
    return get(url, TransactionResponse.class);
  }

  /**
   * Requests <code>GET /transactions/{transactionId}</code>
   * @see <a href="https://www.stellar.org/developers/horizon/reference/transactions-single.html">Transaction Details</a>
   * @param transactionId Transaction to fetch
   * @throws IOException
   */
  public TransactionResponse transaction(String transactionId) throws IOException {
    this.setPathSegments("transactions", transactionId);
    return this.transaction(this.buildUrl());
  }

  /**
   * Builds request to <code>GET /accounts/{account}/transactions</code>
   * @see <a href="https://www.stellar.org/developers/horizon/reference/transactions-for-account.html">Transactions for Account</a>
   * @param account Account for which to get transactions
   */
  public TransactionsRequestBuilder forAccount(KeyPair account) {
    account = checkNotNull(account, "account cannot be null");
    this.setPathSegments("accounts", account.getAccountId(), "transactions");
    return this;
  }

  /**
   * Builds request to <code>GET /ledgers/{ledgerSeq}/transactions</code>
   * @see <a href="https://www.stellar.org/developers/horizon/reference/transactions-for-ledger.html">Transactions for Ledger</a>
   * @param ledgerSeq Ledger for which to get transactions
   */
  public TransactionsRequestBuilder forLedger(long ledgerSeq) {
    this.setPathSegments("ledgers", String.valueOf(ledgerSeq), "transactions");
    return this;
  }

  /**
   * Allows to stream SSE events from horizon.
   * Certain endpoints in Horizon can be called in streaming mode using Server-Sent Events.
   * This mode will keep the connection to horizon open and horizon will continue to return
   * responses as ledgers close.
   * @see <a href="http://www.w3.org/TR/eventsource/" target="_blank">Server-Sent Events</a>
   * @see <a href="https://www.stellar.org/developers/horizon/learn/responses.html" target="_blank">Response Format documentation</a>
   * @param listener {@link EventListener} implementation with {@link TransactionResponse} type
   * @return EventSource object, so you can <code>close()</code> connection when not needed anymore
   */
  /*
  public EventSource stream(final EventListener<TransactionResponse> listener) {
    Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
    WebTarget target = client.target(this.buildUri());
    EventSource eventSource = new EventSource(target) {
      @Override
      public void onEvent(InboundEvent inboundEvent) {
        String data = inboundEvent.readData(String.class);
        if (data.equals("\"hello\"")) {
          return;
        }
        TransactionResponse transaction = GsonSingleton.getInstance().fromJson(data, TransactionResponse.class);
        listener.onEvent(transaction);
      }
    };
    return eventSource;
  }*/

  /**
   * Build and execute request.
   * @return {@link Page} of {@link TransactionResponse}
   * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
   * @throws IOException
   */
  public Page<TransactionResponse> execute() throws IOException, TooManyRequestsException {
    return this.execute(this.buildUrl());
  }

  /**
   * Requests specific <code>uri</code> and returns {@link Page} of {@link TransactionResponse}.
   * This method is helpful for getting the next set of results.
   * @return {@link Page} of {@link TransactionResponse}
   * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
   * @throws IOException
   */
  public Page<TransactionResponse> execute(HttpUrl url) throws IOException, TooManyRequestsException {
    TypeToken typeToken = new TypeToken<Page<TransactionResponse>>() {};
    return get(url, typeToken.getType());
  }

  @Override
  public TransactionsRequestBuilder cursor(String token) {
    super.cursor(token);
    return this;
  }

  @Override
  public TransactionsRequestBuilder limit(int number) {
    super.limit(number);
    return this;
  }

  @Override
  public TransactionsRequestBuilder order(Order direction) {
    super.order(direction);
    return this;
  }
}
