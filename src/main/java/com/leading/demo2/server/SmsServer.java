package com.leading.demo2.server;

import com.leading.demo2.domain.SmsObject;
import org.springframework.http.ResponseEntity;

public interface SmsServer {
    ResponseEntity<String> sendSMS(SmsObject smsObject);
}
