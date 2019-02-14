package com.leading.demo2.server;

import com.google.gson.Gson;
import com.leading.demo2.config.Configuration;
import com.leading.demo2.domain.SmsObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class twilioServerTest {
    private TwilioServer server;
    @Before
    public void setUp() {
        Configuration configuration = new Configuration();
        server = new TwilioServer(configuration, new Gson());
    }

    @Test
    public void testSendSMS() {
        SmsObject object = new SmsObject();
        object.setMessage("Test");
        object.setSender("LD");
        object.setTargetNumbers(Arrays.asList("18017766", "0001"));
        server.sendSMS(object);
    }

}
