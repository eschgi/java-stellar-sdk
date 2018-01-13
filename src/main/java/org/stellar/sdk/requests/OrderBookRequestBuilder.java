package org.stellar.sdk.requests;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.responses.OrderBookResponse;

import java.io.IOException;

/**
 * Builds requests connected to order book.
 */
public class OrderBookRequestBuilder extends RequestBuilder {
  public OrderBookRequestBuilder(OkHttpClient httpClient, HttpUrl serverUrl) {
    super(httpClient, serverUrl, "order_book");
  }

  public OrderBookRequestBuilder buyingAsset(Asset asset) {
    urlBuilder.addQueryParameter("buying_asset_type", asset.getType());
    if (asset instanceof AssetTypeCreditAlphaNum) {
      AssetTypeCreditAlphaNum creditAlphaNumAsset = (AssetTypeCreditAlphaNum) asset;
      urlBuilder.addQueryParameter("buying_asset_code", creditAlphaNumAsset.getCode());
      urlBuilder.addQueryParameter("buying_asset_issuer", creditAlphaNumAsset.getIssuer().getAccountId());
    }
    return this;
  }
  
  public OrderBookRequestBuilder sellingAsset(Asset asset) {
    urlBuilder.addQueryParameter("selling_asset_type", asset.getType());
    if (asset instanceof AssetTypeCreditAlphaNum) {
      AssetTypeCreditAlphaNum creditAlphaNumAsset = (AssetTypeCreditAlphaNum) asset;
      urlBuilder.addQueryParameter("selling_asset_code", creditAlphaNumAsset.getCode());
      urlBuilder.addQueryParameter("selling_asset_issuer", creditAlphaNumAsset.getIssuer().getAccountId());
    }
    return this;
  }

  public OrderBookResponse execute() throws IOException, TooManyRequestsException {
    return this.execute(this.buildUrl());
  }

  public OrderBookResponse execute(HttpUrl url) throws IOException, TooManyRequestsException {
    return get(url, OrderBookResponse.class);
  }

  @Override
  public RequestBuilder cursor(String cursor) {
    throw new RuntimeException("Not implemented yet.");
  }

  @Override
  public RequestBuilder limit(int number) {
    throw new RuntimeException("Not implemented yet.");
  }

  @Override
  public RequestBuilder order(Order direction) {
    throw new RuntimeException("Not implemented yet.");
  }
}
