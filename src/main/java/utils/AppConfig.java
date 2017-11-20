package utils;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Evgeniy Slobozheniuk on 20.11.17.
 */
public class AppConfig {
    private static final Logger log = LogManager.getLogger(AppConfig.class);

    String username;
    String password;
    String applicationKey;
    String certificateFile;
    String certificateKey;
    String httpPostURL;

    public AppConfig(String filePath) {
        Configurations configs = new Configurations();

        try{
            Configuration config = configs.properties(filePath);

            this.username = config.getString("app.username");
            this.password = config.getString("app.password");
            this.applicationKey = config.getString("app.key");
            this.certificateFile = config.getString("app.certificate");
            this.certificateKey = config.getString("app.certificateKey");
            this.httpPostURL = config.getString("app.httpPostURL");

            log.info("Config is loaded");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public String getCertificateFile() {
        return certificateFile;
    }

    public String getCertificateKey() {
        return certificateKey;
    }

    public String getHttpPostURL() {
        return httpPostURL;
    }
}
