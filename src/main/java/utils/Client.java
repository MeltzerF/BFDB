package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import login.LoginResponse;
import login.LogoutResponse;
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

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeniy Slobozheniuk on 20.11.17.
 */
public class Client {
    private static final Logger log = LogManager.getLogger(Client.class);

    private final String loginEndpoint = "/api/certlogin";
    private final String logoutEndpoint = "/api/logout";
    private final AppConfig config;
    private final File certificateFile;

    private int requestID;
    private String sessionToken;

    public Client(AppConfig config, File keyFile) {
        this.certificateFile = keyFile;
        this.config = config;
    }

    public void login() {
        log.info("Starting login...");
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(
                setupSSLContext(
                        getKeyManagers()
                )
        ).build()){
            HttpPost httpPost = new HttpPost(this.config.httpPostURL + this.loginEndpoint);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", this.config.username));
            nvps.add(new BasicNameValuePair("password", this.config.password));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            httpPost.setHeader("X-Application", this.config.applicationKey);
            httpPost.setHeader("Accept", this.config.jsonHeader);

            HttpResponse response = httpClient.execute(httpPost);

            String responseString = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseString = EntityUtils.toString(entity);
                log.debug("Result of login: " + responseString);
            }

            Gson gson = new GsonBuilder().create();
            LoginResponse loginResponse = gson.fromJson(responseString, LoginResponse.class);

            log.info("Server returned login status: " + loginResponse.getLoginStatus());
            log.info("Session sessionToken: " + loginResponse.getSessionToken());

            this.sessionToken = loginResponse.getSessionToken();

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
    }

    public void logout() {
        if (sessionToken != null) {
            log.info("Logging out...");
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(this.config.httpPostURL + this.logoutEndpoint);
                httpPost.setHeader("X-Application", this.config.applicationKey);
                httpPost.setHeader("X-Authentication", this.sessionToken);
                httpPost.setHeader("Accept", this.config.jsonHeader);

                HttpResponse response = httpClient.execute(httpPost);

                String responseString = null;
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseString = EntityUtils.toString(entity);
                    log.debug("Result of logout: " + responseString);
                }

                Gson gson = new GsonBuilder().create();
                LogoutResponse logoutResponse = gson.fromJson(responseString, LogoutResponse.class);

                log.info("Server returned logout status: " + logoutResponse.getStatus());

                this.sessionToken = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("Client is not logged in!");
        }
    }

    public String execute(String operationName, Map<String, Object> params) {
        String requestString;
        //Handling the JSON-RPC
        JsonrpcRequest jsonRequest = new JsonrpcRequest();
        jsonRequest.setId(getRequestID());
        jsonRequest.setMethod(this.config.apiEndpoint + operationName);
        jsonRequest.setParams(params);

        requestString = JsonConverter.convertToJson(jsonRequest);
        log.info(requestString);
        HttpRequest request = new HttpRequest(config);
        return request.sendPostRequestJsonRpc(requestString, operationName, this.sessionToken);
    }

    private String getRequestID() {
        String currentID = String.valueOf(requestID);
        requestID++;
        return currentID;
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

    private KeyManager[] getKeyManagers() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, URISyntaxException {
        FileInputStream keyStream;
        log.info("Key file path: " + this.certificateFile.getPath());
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        keyStream = new FileInputStream(this.certificateFile);
        clientStore.load(keyStream, this.config.certificateKey.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, this.config.certificateKey.toCharArray());
        return kmf.getKeyManagers();
    }
}
