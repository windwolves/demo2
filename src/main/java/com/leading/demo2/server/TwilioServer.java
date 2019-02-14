package com.leading.demo2.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.leading.demo2.config.Configuration;
import com.leading.demo2.domain.SmsObject;
import com.leading.demo2.exception.SmsDemoException;
import com.twilio.Twilio;
import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.leading.demo2.commons.Contants.*;
import static com.leading.demo2.exception.ExceptionCode.CONFIG_ERROR;

@Component
public class TwilioServer implements SmsServer{
    private static final Logger logger = LoggerFactory.getLogger(TwilioServer.class);
    private String twilioCapableNumber;
    private Gson gson;

    @Autowired
    public TwilioServer (Configuration configuration, Gson gson) {
        this.gson = gson;
        init(configuration);
    }

    private void init(Configuration configuration) {

        Map<String, String> credential = configuration.getTwilioCredential();
        if (!credential.containsKey(TWILIO_USER) || !credential.containsKey(TWILIO_PASS) || !credential.containsKey(TWILIO_NUMBER)) {
            logger.error("Cannot load twilio config properties");
            throw new SmsDemoException(CONFIG_ERROR, "Cannot load twilio config properties");
        }

        Twilio.init(credential.get(TWILIO_USER), credential.get(TWILIO_PASS));
        twilioCapableNumber = credential.get(TWILIO_NUMBER);
    }

    @Override
    public ResponseEntity<String> sendSMS(SmsObject smsObject) {
        String message= smsObject.getMessage();
        JsonObject result = new JsonObject();
        JsonArray recipients = new JsonArray();
        smsObject.getTargetNumbers().forEach(n -> {
            try {
                Message.creator(new PhoneNumber(n), new PhoneNumber(twilioCapableNumber), message).create();
                recipients.add(n);
            } catch (ApiConnectionException e) {
                logger.error("Message creation failed: Unable to connect to twilio server.");
            } catch (ApiException e) {
                logger.error("Message creation failed due to: {}.", e.getMessage());
            } catch (Exception e) {
                logger.error("Message creation failed due to: {}", e.getMessage());
            }
        });

        result.add("targetsSent", recipients);
        return new ResponseEntity<>(gson.toJson(result), HttpStatus.OK);
    }

}
