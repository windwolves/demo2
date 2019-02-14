package com.leading.demo2.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leading.demo2.domain.SmsObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SmsControllerTest {
    @Autowired
    private TestRestTemplate rest;
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void testWithValidMessage() throws IOException {
        String smsObjectString = "{\n" +
                "  \"message\": \"TEst\",\n" +
                "  \"targetNumbers\": [\n" +
                "    \"18017766036\",\n" +
                "    \"000\"\n" +
                "  ],\n" +
                "  \"sender\": \"LD\"\n" +
                "}";
        HttpEntity<SmsObject> request = new HttpEntity<>(mapper.readValue(smsObjectString, SmsObject.class));
        ResponseEntity<String> response = rest.exchange("/api/rest/sms", HttpMethod.POST,
                request, String.class);
        String expectedResponseString = "\n" +
                "This message has been sent to the target(s):[18017766036] via textLocal.\n" +
                "This message has been sent to the target(s):[] via twilio.\n" +
                "But failed to send this message to these targets:[000].";
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseString, response.getBody());
    }

    @Test
    public void testWithInvalidMessage() throws IOException {
        String smsObjectString = "{\n" +
                "  \"targetNumbers\": [\n" +
                "    \"18017766036\",\n" +
                "    \"000\"\n" +
                "  ],\n" +
                "  \"sender\": \"LD\"\n" +
                "}";
        HttpEntity<SmsObject> request = new HttpEntity<>(mapper.readValue(smsObjectString, SmsObject.class));
        ResponseEntity<String> response = rest.exchange("/api/rest/sms", HttpMethod.POST,
                request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        String smsObjectString2 = "{\n" +
                "  \"message\": \"TEst\",\n" +
                "  \"targetNumbers\": [\n" +
                "  ],\n" +
                "  \"sender\": \"LD\"\n" +
                "}";
        request = new HttpEntity<>(mapper.readValue(smsObjectString2, SmsObject.class));
        response = rest.exchange("/api/rest/sms", HttpMethod.POST,
                request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        String smsObjectString3 = "{\n" +
                "  \"message\": \"TEst\",\n" +
                "  \"sender\": \"LD\"\n" +
                "}";
        request = new HttpEntity<>(mapper.readValue(smsObjectString3, SmsObject.class));
        response = rest.exchange("/api/rest/sms", HttpMethod.POST,
                request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
