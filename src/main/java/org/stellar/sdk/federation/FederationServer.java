package org.stellar.sdk.federation;

import com.google.common.net.InternetDomainName;
import com.moandjiezana.toml.Toml;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.stellar.sdk.ClientProtocolException;
import org.stellar.sdk.responses.GsonSingleton;

import java.io.IOException;

/**
 * FederationServer handles a network connection to a
 * <a href="https://www.stellar.org/developers/learn/concepts/federation.html" target="_blank">federation server</a>
 * instance and exposes an interface for requests to that instance.
 *
 * For resolving a stellar address without knowing which federation server
 * to query use {@link Federation#resolve(String)}.
 *
 * @see <a href="https://www.stellar.org/developers/learn/concepts/federation.html" target="_blank">Federation docs</a>
 */
public class FederationServer {
  private OkHttpClient httpClient;
  private final HttpUrl serverUrl;
  private final InternetDomainName domain;

  /**
   * Creates a new <code>FederationServer</code> instance.
   * @param httpClient HTTP client
   * @param serverUrl Federation Server URL
   * @param domain Domain name this federation server is responsible for
   * @throws FederationServerInvalidException Federation server is invalid (malformed URL, not HTTPS, etc.)
   */
  public FederationServer(OkHttpClient httpClient, HttpUrl serverUrl, InternetDomainName domain) {
    this.httpClient = httpClient;
    this.serverUrl = serverUrl;
    if (!this.serverUrl.scheme().equals("https")) {
      throw new FederationServerInvalidException();
    }
    this.domain = domain;
  }

  /**
   * Creates a new <code>FederationServer</code> instance.
   * @param httpClient HTTP client
   * @param serverUrl Federation Server URL
   * @param domain Domain name this federation server is responsible for
   * @throws FederationServerInvalidException Federation server is invalid (malformed URL, not HTTPS, etc.)
   */
  public FederationServer(OkHttpClient httpClient, String serverUrl, InternetDomainName domain) {
    this.httpClient = httpClient;
    this.serverUrl = HttpUrl.parse(serverUrl);
    this.domain = domain;
  }

  /**
   * Creates a <code>FederationServer</code> instance for a given domain.
   * It tries to find a federation server URL in stellar.toml file.
   * @see <a href="https://www.stellar.org/developers/learn/concepts/stellar-toml.html" target="_blank">Stellar.toml docs</a>
   * @param httpClient HTTP client
   * @param domain Domain to find a federation server for
   * @throws ConnectionErrorException Connection problems
   * @throws NoFederationServerException Stellar.toml does not contain federation server info
   * @throws FederationServerInvalidException Federation server is invalid (malformed URL, not HTTPS, etc.)
   * @throws StellarTomlNotFoundInvalidException Stellar.toml file was not found or was malformed.
   * @return FederationServer
   */
  public static FederationServer createForDomain(OkHttpClient httpClient, InternetDomainName domain) {
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append("https://");
    urlBuilder.append(domain.toString());
    urlBuilder.append("/.well-known/stellar.toml");
    HttpUrl stellarTomlUrl = HttpUrl.parse(urlBuilder.toString());
    try {
      okhttp3.Request httpRequest = new Request.Builder().url(stellarTomlUrl).get().build();
      okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute();

      if (httpResponse.isSuccessful()) {
        ResponseBody httpResponseBody = httpResponse.body();
        if (httpResponseBody == null) {
          throw new StellarTomlNotFoundInvalidException();
        }

        Toml stellarToml = new Toml().read(httpResponseBody.string());

        String serverUrl = stellarToml.getString("FEDERATION_SERVER");
        if (serverUrl == null) {
          throw new NoFederationServerException();
        }

        return new FederationServer(httpClient, serverUrl, domain);
      } else {
        throw new StellarTomlNotFoundInvalidException();
      }
    } catch (IOException e) {
      throw new ConnectionErrorException();
    }
  }

  /**
   * Resolves a stellar address using a given federation server.
   * @param address Stellar addres, like <code>bob*stellar.org</code>
   * @throws MalformedAddressException Address is malformed
   * @throws ConnectionErrorException Connection problems
   * @throws NotFoundException Stellar address not found by federation server
   * @throws ServerErrorException Federation server responded with error
   * @return FederationResponse
   */
  public FederationResponse resolveAddress(String address) {
    String[] tokens = address.split("\\*");
    if (tokens.length != 2) {
      throw new MalformedAddressException();
    }

    HttpUrl.Builder urlBuilder = this.serverUrl.newBuilder();
    urlBuilder.addQueryParameter("type", "name");
    urlBuilder.addQueryParameter("q", address);
    HttpUrl url = urlBuilder.build();

    try {
      okhttp3.Request httpRequest = new Request.Builder().url(url).get().build();
      okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute();
      if (httpResponse.isSuccessful()) {
        ResponseBody httpResponseBody = httpResponse.body();
        if (httpResponseBody == null) {
          throw new ClientProtocolException("Response contains no content");
        }

        String json = httpResponseBody.string();
        return GsonSingleton.getInstance().fromJson(json, FederationResponse.class);
      } else {
        int statusCode = httpResponse.code();
        if (statusCode == 404) {
          throw new NotFoundException();
        } else {
          throw new ServerErrorException();
        }
      }
    } catch (IOException e) {
      throw new ConnectionErrorException();
    }
  }

  /**
   * Returns a federation server URL.
   * @return URI
   */
  public HttpUrl getServerUrl() {
    return serverUrl;
  }

  /**
   * Returns a domain this server is responsible for.
   * @return InternetDomainName
   */
  public InternetDomainName getDomain() {
    return domain;
  }

}
