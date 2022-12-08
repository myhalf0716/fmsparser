package io.kocowa.fmsparser.common.config;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

  // @Autowired
  private final HttpPoolProperty httpPoolProperty;

  @Bean
  public RestTemplate restTemplate()
    throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
    restTemplate
      .getMessageConverters()
      .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    return restTemplate;
  }

  @Bean
  public ClientHttpRequestFactory httpRequestFactory()
    throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    return new HttpComponentsClientHttpRequestFactory(httpClient());
  }

  @Bean
  public HttpClient httpClient()
    throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    SSLContext sslContext = new SSLContextBuilder()
      .loadTrustMaterial(
        null,
        new TrustStrategy() {
          public boolean isTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
            return true;
          }
        }
      )
      .build();

    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(
      sslContext,
      new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
          return true;
        }
      }
    );

    Registry<ConnectionSocketFactory> registry = RegistryBuilder
      .<ConnectionSocketFactory>create()
      .register("http", PlainConnectionSocketFactory.getSocketFactory())
      .register("https", csf)
      .build();

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
      registry
    );
    connectionManager.setMaxTotal(httpPoolProperty.getMaxTotal());
    connectionManager.setDefaultMaxPerRoute(
      httpPoolProperty.getDefaultMaxPerRoute()
    );
    connectionManager.setValidateAfterInactivity(
      httpPoolProperty.getValidateAfterInactivity()
    );

    RequestConfig requestConfig = RequestConfig
      .custom()
      //The time for the server to return data (response) exceeds the throw of read timeout
      .setSocketTimeout(httpPoolProperty.getReadTimeout())
      //The time to connect to the server (handshake succeeded) exceeds the throw connect timeout
      .setConnectTimeout(httpPoolProperty.getConnectionTimeout())
      //The timeout to get the connection from the connection pool. If the connection is not available after the timeout, the following exception will be thrown
      // org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
      .setConnectionRequestTimeout(
        httpPoolProperty.getConnectionRequestTimeout()
      )
      .build();

    return HttpClientBuilder
      .create()
      .setDefaultRequestConfig(requestConfig)
      .setConnectionManager(connectionManager)
      .setSSLSocketFactory(csf)
      .build();
  }
}
