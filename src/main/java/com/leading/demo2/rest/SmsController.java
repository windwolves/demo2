package com.leading.demo2.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.leading.demo2.domain.SmsObject;
import com.leading.demo2.server.TextLocalServer;
import com.leading.demo2.server.TwilioServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rest")
public class SmsController {
    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);
    private TwilioServer twilioServer;
    private TextLocalServer textLocalServer;
    private Gson gson;
    @Autowired
    public SmsController (TwilioServer twilioServer, TextLocalServer textLocalServer, Gson gson) {
        this.twilioServer = twilioServer;
        this.textLocalServer = textLocalServer;
        this.gson = gson;
    }

    @InitBinder("smsObject")
    public void initBinderLocation(WebDataBinder binder) {
        binder.setDisallowedFields();
    }

    @PostMapping("/sms")
    public ResponseEntity<String> sendSMS(@Valid @RequestBody SmsObject smsObject, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(gson.toJson(buildErrorArray(bindingResult)), HttpStatus.BAD_REQUEST);
        }
        boolean hasTriedWithTwilio = false;
        List<String> remainTargets = smsObject.getTargetNumbers();
        ResponseEntity response;
        try {
            response = textLocalServer.sendSMS(smsObject);
        } catch (Exception e) {
            logger.error("Cannot send message via textLocal due to {}", e.getMessage());
            logger.warn("Try with twilio later");
            response = twilioServer.sendSMS(smsObject);
            hasTriedWithTwilio = true;
        }
        
        return buildResponse(smsObject, response, hasTriedWithTwilio, remainTargets, new StringBuilder());
    }

    private ResponseEntity<String> buildResponse(SmsObject smsObject, ResponseEntity response, boolean hasTriedWithTwilio, List<String> remainTargets, StringBuilder responseBodyBuilder) {
        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null) {
                List<String> targetsSent = new ArrayList<>();
                JsonObject responseBody = gson.fromJson(response.getBody().toString(), JsonObject.class);
                responseBody.getAsJsonArray("targetsSent").forEach(t -> targetsSent.add(t.getAsString()));
                responseBodyBuilder.append("\nThis message has been sent to the target(s):");
                responseBodyBuilder.append(targetsSent.toString());
                responseBodyBuilder.append(" via ");
                responseBodyBuilder.append(hasTriedWithTwilio ? "twilio." : "textLocal.");
                remainTargets.removeAll(targetsSent);
                if (!remainTargets.isEmpty() && !hasTriedWithTwilio) {
                    return retryWithTwilio(smsObject, remainTargets, responseBodyBuilder);
                }
                responseBodyBuilder.append("\nBut failed to send this message to these targets:");
                responseBodyBuilder.append(remainTargets.toString());
                responseBodyBuilder.append(".");
                return new ResponseEntity<>(responseBodyBuilder.toString(), HttpStatus.OK);
            } else {
                logger.error("Something wrong!");
                return new ResponseEntity<>("Internal Error!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            if (!hasTriedWithTwilio) {
                return retryWithTwilio(smsObject, remainTargets, responseBodyBuilder);
            }
            logger.error("Something wrong!");
            return new ResponseEntity<>("Internal Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private ResponseEntity<String> retryWithTwilio(SmsObject smsObject, List<String> remainTargets, StringBuilder responseBodyBuilder) {
        smsObject.setTargetNumbers(remainTargets);
        ResponseEntity<String> res = twilioServer.sendSMS(smsObject);
        return buildResponse(smsObject, res, true, remainTargets, responseBodyBuilder);
    }
    
    //build error array from validation
    private static JsonArray buildErrorArray(BindingResult result) {
        List<ObjectError> errors = result.getAllErrors();
        JsonArray errorJsonArray = new JsonArray();
        errors.forEach(e -> {
            JsonObject errorObject = new JsonObject();
            if (e instanceof FieldError) {
                FieldError fieldError = (FieldError) e;
                errorObject.addProperty("field", fieldError.getField());
                if (fieldError.getRejectedValue() != null) {
                    errorObject.addProperty("rejectedValue", fieldError.getRejectedValue().toString());
                }
            }
            errorObject.addProperty("errorMessage", e.getDefaultMessage());
            errorJsonArray.add(errorObject);
        });
        return errorJsonArray;
    }
}
