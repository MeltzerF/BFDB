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
import utils.AppConfig;

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
public class Client {
    private static final Logger log = LogManager.getLogger(Client.class);

    private final String loginEndpoint = "/api/certlogin";
    private final String logoutEndpoint = "/api/logout";
    private final String certificateKey;
    private final String username;
    private final String password;
    private final String applicationKey;
    private final String httpPostURL;
    private final File certificateFile;

    private String sessionToken;

    public Client(AppConfig config, File keyFile) {
        this.certificateFile = keyFile;
        this.certificateKey = config.getCertificateKey();
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.applicationKey = config.getApplicationKey();
        this.httpPostURL = config.getHttpPostURL();
    }

    public void login() {
        log.info("Starting login...");
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(
                setupSSLContext(
                        getKeyManagers()
                )
        ).build()){
            HttpPost httpPost = new HttpPost(this.httpPostURL + this.loginEndpoint);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", this.username));
            nvps.add(new BasicNameValuePair("password", this.password));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            httpPost.setHeader("X-Application", this.applicationKey);
            httpPost.setHeader("Accept", "application/json");

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
        if (!sessionToken.isEmpty()) {
            log.info("Logging out...");
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(this.httpPostURL + this.logoutEndpoint);
                httpPost.setHeader("X-Application", this.applicationKey);
                httpPost.setHeader("X-Authentication", this.sessionToken);
                httpPost.setHeader("Accept", "application/json");

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

    private KeyManager[] getKeyManagers() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, URISyntaxException {
        FileInputStream keyStream;
        log.info("Key file path: " + this.certificateFile.getPath());
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        keyStream = new FileInputStream(this.certificateFile);
        clientStore.load(keyStream, this.certificateKey.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, this.certificateKey.toCharArray());
        return kmf.getKeyManagers();
    }
}
