package org.stellar.sdk;

import okhttp3.*;
import org.stellar.sdk.requests.*;
import org.stellar.sdk.responses.GsonSingleton;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;

/**
 * Main class used to connect to Horizon server.
 */
public class Server {
    private OkHttpClient httpClient;
    private HttpUrl serverUrl;

    public Server(String serverUrl) {
        this(new OkHttpClient.Builder().build(), serverUrl);
    }

    public Server(OkHttpClient httpClient, String serverUrl) {
        this.httpClient = httpClient;
        this.serverUrl = HttpUrl.parse(serverUrl);
    }

    /**
     * Returns {@link AccountsRequestBuilder} instance.
     */
    public AccountsRequestBuilder accounts() {
        return new AccountsRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link EffectsRequestBuilder} instance.
     */
    public EffectsRequestBuilder effects() {
        return new EffectsRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link LedgersRequestBuilder} instance.
     */
    public LedgersRequestBuilder ledgers() {
        return new LedgersRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link OffersRequestBuilder} instance.
     */
    public OffersRequestBuilder offers() {
        return new OffersRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link OperationsRequestBuilder} instance.
     */
    public OperationsRequestBuilder operations() {
        return new OperationsRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link OrderBookRequestBuilder} instance.
     */
    public OrderBookRequestBuilder orderBook() {
        return new OrderBookRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link TradesRequestBuilder} instance.
     */
    public TradesRequestBuilder trades() {
        return new TradesRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link PathsRequestBuilder} instance.
     */
    public PathsRequestBuilder paths() {
        return new PathsRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link PaymentsRequestBuilder} instance.
     */
    public PaymentsRequestBuilder payments() {
        return new PaymentsRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Returns {@link TransactionsRequestBuilder} instance.
     */
    public TransactionsRequestBuilder transactions() {
        return new TransactionsRequestBuilder(httpClient, serverUrl);
    }

    /**
     * Submits transaction to the network.
     * @param transaction transaction to submit to the network.
     * @return {@link SubmitTransactionResponse}
     * @throws IOException
     */
    public SubmitTransactionResponse submitTransaction(Transaction transaction) throws IOException {
        HttpUrl.Builder urlBuilder = serverUrl.newBuilder();
        urlBuilder.addPathSegment("transactions");
        HttpUrl transactionsUrl = urlBuilder.build();

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("tx",  transaction.toEnvelopeXdrBase64());
        FormBody formBody = formBodyBuilder.build();

        okhttp3.Request httpRequest = new Request.Builder().url(transactionsUrl).post(formBody).build();
        okhttp3.Response httpResponse = this.httpClient.newCall(httpRequest).execute();

        if (httpResponse.isSuccessful()) {
            ResponseBody httpResponseBody = httpResponse.body();
            if (httpResponseBody == null) {
                throw new ClientProtocolException("Response contains no content");
            }

            String json = httpResponseBody.string();
            return GsonSingleton.getInstance().fromJson(json, SubmitTransactionResponse.class);
        } else {
            int statusCode = httpResponse.code();
            String statusMessage = httpResponse.message();
            throw new HttpResponseException(statusCode, statusMessage);
        }

    }
}
