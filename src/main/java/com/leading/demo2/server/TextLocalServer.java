package com.leading.demo2.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.leading.demo2.config.Configuration;
import com.leading.demo2.domain.SmsObject;
import com.leading.demo2.exception.SmsDemoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import static com.leading.demo2.commons.Contants.MESSAGE;
import static com.leading.demo2.commons.Contants.TEXTLOCAL_SMS_SEND_URL;
import static com.leading.demo2.exception.ExceptionCode.CONFIG_ERROR;

@Component
public class TextLocalServer implements SmsServer {
    private static final Logger logger = LoggerFactory.getLogger(TextLocalServer.class);

    private String apiKey;
    private RestTemplate restTemplate;
    private static final String SUCCESS = "success";
    private Gson gson;

    @Autowired
    public TextLocalServer(Configuration configuration, Gson gson) {
        restTemplate = new RestTemplate();
        this.gson = gson;
        load(configuration);
    }

    private void load(Configuration configuration) {
        if (StringUtils.isEmpty(configuration.getTextLocalApiKey())) {
            logger.error("Cannot load textLocalApiKey.");
            throw new SmsDemoException(CONFIG_ERROR, "Cannot load textLocalApiKey");
        }
        apiKey = configuration.getTextLocalApiKey();
    }

    @Override
    public ResponseEntity<String> sendSMS(SmsObject smsObject) {
        String numbers = smsObject.getTargetNumbersString();
        String message = smsObject.getMessage();
        String sender = smsObject.getSender();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("apikey", apiKey);
        map.add("numbers", numbers);
        map.add(MESSAGE, message);
        if (!StringUtils.isEmpty(sender)) {
            map.add("sender", sender);
        }
//        map.add("test", "true");//Set as true to mark this message is a test. so the server will not actually send message

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.exchange(TEXTLOCAL_SMS_SEND_URL, HttpMethod.POST, entity, String.class);
        if (response != null) {
            JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);
            if (SUCCESS.equals(responseBody.get("status").getAsString())) {
                JsonObject result = new JsonObject();
                JsonArray recipients = new JsonArray();
                responseBody.getAsJsonArray("messages").forEach(m -> recipients.add(m.getAsJsonObject().get("recipient")));
                result.add("targetsSent", recipients);
                return new ResponseEntity<>(gson.toJson(result), HttpStatus.OK);
            } else {
                if (responseBody.getAsJsonArray("errors").size() > 0) {
                    JsonObject error = responseBody.getAsJsonArray("errors").get(0).getAsJsonObject();
                    logger.error(error.get(MESSAGE).getAsString());
                    if ("7".equals(error.get("code").getAsString())) {//"7" means Insufficient credits.
                        return new ResponseEntity<>(error.get(MESSAGE).getAsString(), HttpStatus.SERVICE_UNAVAILABLE);
                    } else {
                        return new ResponseEntity<>(error.get(MESSAGE).getAsString(), HttpStatus.BAD_REQUEST);
                    }
                } else {
                    logger.error("No error content in failed response");
                    return new ResponseEntity<>("No error content in failed response", HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }
        }

        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
