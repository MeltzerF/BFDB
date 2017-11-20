package utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Evgeniy Slobozheniuk on 21.11.17.
 */
public class HttpRequest {
    private static final Logger log = LogManager.getLogger(HttpRequest.class);

    private AppConfig config = null;

    private final String HTTP_HEADER_X_APPLICATION = "X-Application";
    private final String HTTP_HEADER_X_AUTHENTICATION = "X-Authentication";
    private final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private final String HTTP_HEADER_ACCEPT = "Accept";
    private final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";
    private final String HTTP_HEADER_CONNECTION = "Connection";

    public HttpRequest() {
        super();
    }

    public HttpRequest(AppConfig config) {
        this.config = config;
    }

    private String sendPostRequest(String param, String operation, String ssoToken, String URL, ResponseHandler<String> reqHandler) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setHeader(HTTP_HEADER_CONTENT_TYPE, config.jsonHeader);
            httpPost.setHeader(HTTP_HEADER_ACCEPT, config.jsonHeader);
            httpPost.setHeader(HTTP_HEADER_ACCEPT_CHARSET, config.encoding);
            httpPost.setHeader(HTTP_HEADER_X_APPLICATION, config.applicationKey);
            httpPost.setHeader(HTTP_HEADER_X_AUTHENTICATION, ssoToken);
            httpPost.setHeader(HTTP_HEADER_CONNECTION, config.jsonKeepAlive);

            httpPost.setEntity(new StringEntity(param, config.encoding));

            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                log.debug("Result of " + operation + ": " + responseString);
                return responseString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendPostRequestJsonRpc(String param, String operation, String ssoToken) {
        String apiNgURL = config.apiNgUrl + config.jsonSuffix;
        return sendPostRequest(param, operation, ssoToken, apiNgURL, new JsonResponseHandler(config));

    }
}
