package com.leading.demo2.server;


import com.google.gson.Gson;
import com.leading.demo2.config.Configuration;
import com.leading.demo2.domain.SmsObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TextLocalServerTest {
    private TextLocalServer server;

    @Before
    public void setUp() {
        Configuration configuration = new Configuration();
        server = new TextLocalServer(configuration, new Gson());
    }

    @Test
    public void testSendSMS() {
        SmsObject object = new SmsObject();
        object.setMessage("TEst");
        object.setSender("LD");
        object.setTargetNumbers(Arrays.asList("18017766", "000"));
        server.sendSMS(object);
    }
}
