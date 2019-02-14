package com.leading.demo2.config;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.leading.demo2.commons.Contants.*;

@Component
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private Properties properties;
    private static Gson gson = new Gson();
    public Configuration() {
        init();
    }

    private void init() {
        properties = new Properties();
        try {
            properties.load(Configuration.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            logger.error("Cannot load default config property file");
            //TODO throw exception
        }
    }

    public Map<String, String> getTwilioCredential() {
        Map<String, String> twilioCredential = new HashMap<>();
        if (properties.getProperty(TWILIO_USER) == null
                ||properties.getProperty(TWILIO_PASS) == null) {
            logger.error("Cannot load twilio config properties");
        }
        twilioCredential.put(TWILIO_USER, properties.getProperty(TWILIO_USER));
        twilioCredential.put(TWILIO_PASS, properties.getProperty(TWILIO_PASS));
        twilioCredential.put(TWILIO_NUMBER, properties.getProperty(TWILIO_NUMBER));
        return twilioCredential;
    }

    public String getTextLocalApiKey() {
        if (properties.getProperty(TEXTLOCAL_API_KEY) == null) {
            logger.error("Cannot load textLocal config properties");
        }
        return properties.getProperty(TEXTLOCAL_API_KEY);
    }

    public Gson getGson(){
        return gson;
    }
}
