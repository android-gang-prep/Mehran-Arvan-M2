package com.example.mehranarvanm2;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WeatherTest {
    private final String fakeData = "<?xml version=\"1.0\" encoding=\"utf-8\"?><meteomatics-api-response version=\"3.0\"><user>cloud_maghari_setare</user><dateGenerated>2024-04-21T06:37:39Z</dateGenerated><status>OK</status><data><parameter name=\"wind_speed_10m:ms\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T13:00:00Z\">2.4</value></location></parameter><parameter name=\"wind_dir_10m:d\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T13:00:00Z\">214.7</value></location></parameter><parameter name=\"t_2m:F\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T13:00:00Z\">77.4</value></location></parameter><parameter name=\"weather_symbol_1h:idx\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T13:00:00Z\">3</value></location></parameter><parameter name=\"precip_24h:mm\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T13:00:00Z\">1.51</value></location></parameter><parameter name=\"uv:idx\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T13:00:00Z\">1</value></location></parameter><parameter name=\"sunset:sql\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T13:00:00Z\">2024-04-20T15:11:00Z</value></location></parameter><parameter name=\"sunrise:sql\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T00:00:00Z\">2024-04-20T01:54:00Z</value></location></parameter><parameter name=\"t_max_2m_24h:C\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T00:00:00Z\">27.6</value></location></parameter><parameter name=\"t_min_2m_24h:C\"><location lat=\"35.721324\" lon=\"51.342037\"><value date=\"2024-04-20T00:00:00Z\">11.7</value></location></parameter></data></meteomatics-api-response>";


    private SimpleDateFormat testDateFormatter;
    private  SimpleDateFormat defaultDateFormat;

    @Before
    public void createDB() {
        testDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        testDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        defaultDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    @Test
    public void callApiAndParseXML() throws InterruptedException, java.text.ParseException {

        RepoNetwork repoNetwork = new RepoNetwork();
        repoNetwork.enableTest(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) {
                return new Response.Builder().code(200).request(repoNetwork.request).protocol(Protocol.HTTP_1_1).message("OK").body(ResponseBody.create(fakeData, MediaType.parse("application/xml"))).build();
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        repoNetwork.getData((data, isSuccess) -> {
            assert isSuccess;
            countDownLatch.countDown();

        });

        countDownLatch.await();
        try {
            Assert.assertEquals(repoNetwork.getWeather("wind_speed").get(0),"2.4");
            Assert.assertEquals (repoNetwork.getWeather("wind_dir").get(0),("214.7"));
            Assert.assertEquals (repoNetwork.getWeather("t_2m").get(0),("77.4"));
            String date_tmp = repoNetwork.getWeather("t_2m", true).get(0);
            Date date = defaultDateFormat.parse(date_tmp);
            Assert.assertEquals(testDateFormatter.format(date),"2024-04-20 13:00:00");
            Assert.assertEquals (repoNetwork.getWeather("weather_symbol_1h").get(0),("3"));
            Assert.assertEquals (repoNetwork.getWeather("precip_24h").get(0),("1.51"));
            Assert.assertEquals (repoNetwork.getWeather("uv").get(0),("1"));
            Assert.assertEquals (testDateFormatter.format(defaultDateFormat.parse(repoNetwork.getWeather("sunset").get(0))),("2024-04-20 15:11:00"));
            Assert.assertEquals (testDateFormatter.format(defaultDateFormat.parse(repoNetwork.getWeather("sunrise").get(0))),("2024-04-20 01:54:00"));
            Assert.assertEquals (repoNetwork.getWeather("t_max_2m").get(0),("27.6"));
            Assert.assertEquals (repoNetwork.getWeather("t_min_2m").get(0),("11.7"));
        } catch (ParseException e) {
            e.printStackTrace();
            assert false;
        }

    }

    @Test
    public void testErrorServer() throws InterruptedException {
        RepoNetwork repoNetwork = new RepoNetwork();
        repoNetwork.enableTest(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) {
                return new Response.Builder().code(404).build();
            }
        });

        CountDownLatch countDownLatch = new CountDownLatch(1);
        repoNetwork.getData((data, isSuccess) -> {
            Assert.assertFalse(isSuccess);
            countDownLatch.countDown();

        });

        countDownLatch.await();
    }

}
