package org.stellar.sdk.requests;

import com.google.gson.reflect.TypeToken;

import org.apache.http.client.fluent.Request;
import org.stellar.base.Keypair;
import org.stellar.sdk.Page;
import org.stellar.sdk.effects.Effect;

import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

public class EffectsRequestBuilder extends RequestBuilder {
  public EffectsRequestBuilder(URI serverURI) {
    super(serverURI, "effects");
  }

  public EffectsRequestBuilder forAccount(Keypair account) {
    account = checkNotNull(account, "account cannot be null");
    this.addSegments("accounts", account.getAddress(), "effects");
    return this;
  }

  public EffectsRequestBuilder forLedger(long ledgerSeq) {
    this.addSegments("ledgers", String.valueOf(ledgerSeq), "effects");
    return this;
  }

  public EffectsRequestBuilder forTransaction(String transactionId) {
    transactionId = checkNotNull(transactionId, "transactionId cannot be null");
    this.addSegments("transactions", transactionId, "effects");
    return this;
  }

  public EffectsRequestBuilder forOperation(long operationId) {
    this.addSegments("operations", String.valueOf(operationId), "effects");
    return this;
  }

  public Page<Effect> execute() throws IOException {
    TypeToken type = new TypeToken<Page<Effect>>() {};
    ResponseHandler<Page<Effect>> responseHandler = new ResponseHandler<Page<Effect>>(type);
    return (Page<Effect>) Request.Get(this.buildUri()).execute().handleResponse(responseHandler);
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