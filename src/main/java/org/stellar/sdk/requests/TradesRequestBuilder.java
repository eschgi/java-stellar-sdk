package org.stellar.sdk.requests;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.responses.TradeResponse;

import java.io.IOException;

/**
 * Builds requests connected to trades.
 */
public class TradesRequestBuilder extends RequestBuilder {
    public TradesRequestBuilder(OkHttpClient httpClient, HttpUrl serverUrl) {
        super(httpClient, serverUrl, "order_book/trades");
    }

    public TradesRequestBuilder buyingAsset(Asset asset) {
        urlBuilder.addQueryParameter("buying_asset_type", asset.getType());
        if (asset instanceof AssetTypeCreditAlphaNum) {
            AssetTypeCreditAlphaNum creditAlphaNumAsset = (AssetTypeCreditAlphaNum) asset;
            urlBuilder.addQueryParameter("buying_asset_code", creditAlphaNumAsset.getCode());
            urlBuilder.addQueryParameter("buying_asset_issuer", creditAlphaNumAsset.getIssuer().getAccountId());
        }
        return this;
    }

    public TradesRequestBuilder sellingAsset(Asset asset) {
        urlBuilder.addQueryParameter("selling_asset_type", asset.getType());
        if (asset instanceof AssetTypeCreditAlphaNum) {
            AssetTypeCreditAlphaNum creditAlphaNumAsset = (AssetTypeCreditAlphaNum) asset;
            urlBuilder.addQueryParameter("selling_asset_code", creditAlphaNumAsset.getCode());
            urlBuilder.addQueryParameter("selling_asset_issuer", creditAlphaNumAsset.getIssuer().getAccountId());
        }
        return this;
    }

    public TradeResponse execute() throws IOException, TooManyRequestsException {
        return execute(buildUrl());
    }

    public TradeResponse execute(HttpUrl url) throws IOException, TooManyRequestsException {
        return get(url, TradeResponse.class);
    }
}
