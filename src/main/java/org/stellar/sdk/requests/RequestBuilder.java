package org.stellar.sdk.requests;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.stellar.sdk.ClientProtocolException;
import org.stellar.sdk.HttpResponseException;
import org.stellar.sdk.responses.GsonSingleton;
import org.stellar.sdk.responses.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Abstract class for request builders.
 */
public abstract class RequestBuilder {
  private OkHttpClient httpClient;
  protected HttpUrl.Builder urlBuilder;
  private ArrayList<String> segments;
  private boolean segmentsAdded;

  RequestBuilder(OkHttpClient httpClient, HttpUrl serverUrl, String defaultSegment) {
    this.httpClient = httpClient;
    this.urlBuilder = serverUrl.newBuilder();
    this.segments = new ArrayList<String>();
    if (defaultSegment != null) {
      this.setSegments(defaultSegment);
    }
    this.segmentsAdded = false; // Allow overwriting segments
  }

  protected RequestBuilder setSegments(String... segments) {
    if (segmentsAdded) {
      throw new RuntimeException("URL segments have been already added.");
    }

    segmentsAdded = true;
    this.segments.clear();
    for (String segment : segments) {
      this.segments.add(segment);
    }

    return this;
  }

  /**
   * Sets <code>cursor</code> parameter on the request.
   * A cursor is a value that points to a specific location in a collection of resources.
   * The cursor attribute itself is an opaque value meaning that users should not try to parse it.
   * @see <a href="https://www.stellar.org/developers/horizon/reference/resources/page.html">Page documentation</a>
   * @param cursor
   */
  public RequestBuilder cursor(String cursor) {
    urlBuilder.addQueryParameter("cursor", cursor);
    return this;
  }

  /**
   * Sets <code>limit</code> parameter on the request.
   * It defines maximum number of records to return.
   * For range and default values check documentation of the endpoint requested.
   * @param number maxium number of records to return
   */
  public RequestBuilder limit(int number) {
    urlBuilder.addQueryParameter("limit", String.valueOf(number));
    return this;
  }

  /**
   * Sets <code>order</code> parameter on the request.
   * @param direction {@link org.stellar.sdk.requests.RequestBuilder.Order}
   */
  public RequestBuilder order(Order direction) {
    urlBuilder.addQueryParameter("order", direction.getValue());
    return this;
  }

  protected <TResponse> TResponse get(final HttpUrl url,
                                      final Class<TResponse> classOfResponse) throws IOException {
    return request(new Request.Builder().url(url).get(), classOfResponse);
  }

  protected <TResponse> TResponse get(final HttpUrl url,
                                      final Type typeOfResponse) throws IOException {
    return request(new Request.Builder().url(url).get(), typeOfResponse);
  }

  protected <TResponse> TResponse request(final Request.Builder httpRequestBuilder,
                                          final Class<TResponse> classOfResponse) throws IOException {
    return request(httpRequestBuilder, classOfResponse, null);
  }

  protected <TResponse> TResponse request(final Request.Builder httpRequestBuilder,
                                          final Type typeOfResponse) throws IOException {
    return request(httpRequestBuilder, null, typeOfResponse);
  }

  private <TResponse> TResponse request(final Request.Builder httpRequestBuilder,
                                        final Class<TResponse> classOfResponse,
                                        final Type typeOfResponse) throws IOException {
    okhttp3.Request httpRequest = httpRequestBuilder.build();
    okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute();
    if (httpResponse.isSuccessful()) {
      ResponseBody httpResponseBody = httpResponse.body();
      if (httpResponseBody == null) {
        throw new ClientProtocolException("Response contains no content");
      }

      String json = httpResponseBody.string();

      /*
       * "Generics on a type are typically erased at runtime, except when the type is compiled with the
       * generic parameter bound. In that case, the compiler inserts the generic type information into
       * the compiled class. In other cases, that is not possible."
       * More info: http://stackoverflow.com/a/14506181
       */
      TResponse response;
      if (typeOfResponse != null) {
        response = GsonSingleton.getInstance().fromJson(json, typeOfResponse);
      } else {
        response = GsonSingleton.getInstance().fromJson(json, classOfResponse);
      }

      if (response instanceof Response) {
        ((Response) response).setHeaders(httpResponse.headers());
      }

      return response;
    } else {
      int statusCode = httpResponse.code();

      // Too Many Requests
      if (statusCode == 429) {
        String retryAfterValue = httpResponse.header("Retry-After");
        if (retryAfterValue != null) {
          int retryAfter = Integer.parseInt(retryAfterValue);
          throw new TooManyRequestsException(retryAfter);
        } else {
          throw new TooManyRequestsException(0);
        }
      }

      String statusMessage = httpResponse.message();
      throw new HttpResponseException(statusCode, statusMessage);
    }
  }

  HttpUrl buildUrl() {
    for (String segment : this.segments) {
      this.urlBuilder.addPathSegment(segment);
    }
    return urlBuilder.build();
  }

  /**
   * Represents possible <code>order</code> parameter values.
   */
  public enum Order {
    ASC("asc"),
    DESC("desc");
    private final String value;
    Order(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }
  }
}
