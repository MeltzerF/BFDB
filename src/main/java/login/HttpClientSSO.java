package login;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.AppConfig;
import utils.JsonConverter;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeniy Slobozheniuk on 20.11.17.
 */
public class HttpClientSSO {
    private static final Logger log = LogManager.getLogger(HttpClientSSO.class);

    String token;

    public HttpClientSSO(AppConfig config, File keyFile) {
        this.token = login(config, keyFile);
    }

    private String login(AppConfig config, File keyFile) {
        log.info("Starting authentication...");
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(
                setupSSLContext(
                        getKeyManagers(config, keyFile)
                )
        ).build()){
            HttpResponse response = httpClient.execute(getHttpPost(config));

            String responseString = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseString = EntityUtils.toString(entity);
            }

            Gson gson = new GsonBuilder().create();
            LoginResponse loginResponse = gson.fromJson(responseString, LoginResponse.class);

            log.info("Server returned status: " + loginResponse.getLoginStatus());
            log.info("Session token: " + loginResponse.getSessionToken());

            return loginResponse.getSessionToken();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void logout() {
        if (!token.isEmpty()) {
            //TODO: Proper logout
        }
    }

    private SSLConnectionSocketFactory setupSSLContext(KeyManager[] kms) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kms, null, new SecureRandom());

        return new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
    }

    private KeyManager[] getKeyManagers(AppConfig config, File keyFile) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, URISyntaxException {
        FileInputStream keyStream;
        log.info("Key file path: " + keyFile.getPath());
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        keyStream = new FileInputStream(keyFile);
        clientStore.load(keyStream, config.getCertificateKey().toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, config.getCertificateKey().toCharArray());
        return kmf.getKeyManagers();
    }

    private HttpPost getHttpPost(AppConfig config) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(config.getHttpPostURL());
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", config.getUsername()));
        nvps.add(new BasicNameValuePair("password", config.getPassword()));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        httpPost.setHeader("X-Application", config.getApplicationKey());
        return httpPost;
    }
}
