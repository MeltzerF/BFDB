package utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Evgeniy Slobozheniuk on 24.11.17.
 */
public class JsonResponseHandler implements ResponseHandler<String> {
    private AppConfig config;

    public JsonResponseHandler(AppConfig config) {
        this.config = config;
    }

    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }

        HttpEntity entity = httpResponse.getEntity();
        return entity == null ? null : EntityUtils.toString(entity, config.encoding);
    }
}
