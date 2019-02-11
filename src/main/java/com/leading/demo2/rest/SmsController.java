package com.leading.demo2.rest;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rest")
public class SmsController {
    public SmsController () {
        Twilio.init("AC3f1cfb4a01400fe099516067ea3a67a1", "09200dbd2b26c53194c1f1231516d015");
    }
    @PostMapping("/sms")
    public ResponseEntity<String> sendSMS() {
        Message SMS = Message.creator(new PhoneNumber("+8618017766036"), new PhoneNumber("+18058521966"), "Test").create();
        System.out.println(SMS.getSid());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
