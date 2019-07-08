package com.intuit.benten.splunk.MockServer;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.intuit.benten.common.actionhandlers.BentenHandlerResponse;
import com.intuit.benten.common.actionhandlers.BentenSlackResponse;
import com.intuit.benten.splunk.actionhandlers.SplunkUserLogsActionHandler;
import com.intuit.benten.splunk.config.AppConfig;
import com.intuit.benten.splunk.helpers.Expectations;
import com.intuit.benten.splunk.helpers.MessageBuilder;
import com.intuit.benten.splunk.http.SplunkHttpClient;
import com.intuit.benten.splunk.properties.SplunkProperties;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableAutoConfiguration
//@ActiveProfiles("mock")
@ContextConfiguration(classes = {SplunkUserLogsActionHandler.class, SplunkHttpClient.class, SplunkProperties.class, AppConfig.class})
@ComponentScan("com.intuit.benten.splunk.actionhandlers")
public class SplunkMockServer {
    private static final String expectedApplicationId = "83A3E4B8-0616-4B64-8C55-C34EFDA351C9";
    private static ClientAndServer mockServer;
    @Autowired
    private SplunkUserLogsActionHandler splunkUserLogsActionHandler;

    @BeforeClass
    public static void setUpMockServer() {
        mockServer = startClientAndServer(8089);
        Expectations.createDefaultExpectations(mockServer);
    }

    @AfterClass
    public static void detachMockServer() {
        mockServer.stop();
    }

    @Test
    public void testHandleResponse() {
        //TODO
        JsonElement jsonElement = new JsonPrimitive("1234");
        BentenHandlerResponse bentenHandlerResponse = splunkUserLogsActionHandler.handle(MessageBuilder.constructBentenUserLogsMessage(jsonElement));

        Assert.assertNotNull(bentenHandlerResponse.getBentenSlackResponse());
        Assert.assertNotNull(bentenHandlerResponse.getSlackText());
    }

    @Test
    public void testGetApplicationId() {
        String authCode = "1234";
        try {
            String applicationId = splunkUserLogsActionHandler.getApplicationId(authCode);
            Assert.assertEquals(applicationId, expectedApplicationId);
            System.out.println("Application id = " + applicationId);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBuildMessageHelperPreTextOnly() {
        HashMap<String, String> transaction = new HashMap<>();
        String key = "key";
        String additionalText = "additionalText";
        transaction.put(key, "value");

        String response = splunkUserLogsActionHandler.buildMessageHelper(transaction, key, additionalText);
        String expectedResponse = "*" + additionalText + "*" + " `" + transaction.get(key) + "`\n";

        System.out.println("Received Response = " + response);
        System.out.println("Expected Response = " + expectedResponse);
        Assert.assertEquals(response, expectedResponse);
    }

    @Test
    public void testBuildMessageHelperPreAndPostText() {
        HashMap<String, String> transaction = new HashMap<>();
        String key = "key";
        String preText = "preText";
        String postText = "postText";

        transaction.put(key, "value");

        String response = splunkUserLogsActionHandler.buildMessageHelper(transaction, key, preText, postText);
        String expectedResponse = preText + " `" + transaction.get(key) + "` " + postText + "\n";

        System.out.println("Received Response = " + response);
        System.out.println("Expected Response = " + expectedResponse);
        Assert.assertEquals(response, expectedResponse);
    }

    @Test
    public void testGenerateMeaningfulInfo() {
        //TODO
        ArrayList<HashMap<String, String>> listOfTransactions;
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        try {
            BufferedReader br = new BufferedReader(new FileReader("com/intuit/benten/splunk/list-of-transactions.json"));
            JsonElement jsonElement = jsonParser.parse(br);

            //Create generic type
            Type type = new TypeToken<List<Pattern>>() {
            }.getType();
            listOfTransactions = gson.fromJson(jsonElement, type);
            BentenSlackResponse bentenSlackResponse = splunkUserLogsActionHandler.generateMeaningfulInfo(listOfTransactions);

            System.out.println(bentenSlackResponse.getSlackText());

            Assert.assertNotNull(bentenSlackResponse);
            Assert.assertNotNull(bentenSlackResponse.getSlackText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

