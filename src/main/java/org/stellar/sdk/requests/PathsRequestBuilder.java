package org.stellar.sdk.requests;

import com.google.gson.reflect.TypeToken;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.PathResponse;

import java.io.IOException;

/**
 * Builds requests connected to paths.
 */
public class PathsRequestBuilder extends RequestBuilder {
  public PathsRequestBuilder(OkHttpClient okHttpClient, HttpUrl serverUrl) {
    super(okHttpClient, serverUrl, "paths");
  }

  public PathsRequestBuilder destinationAccount(KeyPair account) {
    urlBuilder.addQueryParameter("destination_account", account.getAccountId());
    return this;
  }

  public PathsRequestBuilder sourceAccount(KeyPair account) {
    urlBuilder.addQueryParameter("source_account", account.getAccountId());
    return this;
  }

  public PathsRequestBuilder destinationAmount(String amount) {
    urlBuilder.addQueryParameter("destination_amount", amount);
    return this;
  }

  public PathsRequestBuilder destinationAsset(Asset asset) {
    urlBuilder.addQueryParameter("destination_asset_type", asset.getType());
    if (asset instanceof AssetTypeCreditAlphaNum) {
      AssetTypeCreditAlphaNum creditAlphaNumAsset = (AssetTypeCreditAlphaNum) asset;
      urlBuilder.addQueryParameter("destination_asset_code", creditAlphaNumAsset.getCode());
      urlBuilder.addQueryParameter("destination_asset_issuer", creditAlphaNumAsset.getIssuer().getAccountId());
    }
    return this;
  }

  /**
   * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
   * @throws IOException
   */
  public Page<PathResponse> execute() throws IOException, TooManyRequestsException {
    return this.execute(this.buildUrl());
  }

  /**
   * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
   * @throws IOException
   */
  public Page<PathResponse> execute(HttpUrl url) throws IOException, TooManyRequestsException {
    TypeToken typeToken = new TypeToken<Page<PathResponse>>() {};
    return get(url, typeToken.getType());
  }
}
