package com.example.mehranarvanm2;

import android.util.Log;

import androidx.annotation.NonNull;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RepoNetwork {
    private OkHttpClient client;

    private final String username = "cloud_maghari_setare";
    private final String password = "0NI7zP87ou";
    public Request request;
    private XmlParserData xmlParserData;

    public RepoNetwork() {
        this.client = new OkHttpClient();
        String base64 = java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = dateFormat.format(new Date());
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("http://api.meteomatics.com/" + date + "ZP1D:PT1H/wind_speed_10m:ms,wind_dir_10m:d,t_2m:F,weather_symbol_1h:idx,precip_24h:mm,uv:idx,sunset:sql,sunrise:sql,t_max_2m_24h:C,t_min_2m_24h:C/35.721324,51.342037/xml");
        requestBuilder.addHeader("Authorization", "Basic " + base64);
        request = requestBuilder.build();
    }

    public void enableTest(Interceptor interceptor) {
        client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    private static RepoNetwork repoNetwork;

    public static RepoNetwork getInstance() {
        if (repoNetwork == null) repoNetwork = new RepoNetwork();

        return repoNetwork;
    }

    public void getData(CallBack callBack) {
        if (xmlParserData != null) return;
        callData( callBack);

    }

    public List<String> getWeather(String name) {
        return getWeather(name,false);
    }

    public List<String> getWeather(String name, boolean date) {
        if (xmlParserData==null)
            return new ArrayList<>();
        List<String> list = xmlParserData.get(name, date);
        return list;
    }

    private void callData(CallBack callBack) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getMessage());

                callBack.onResponse(null, false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {

                        xmlParserData = new XmlParserData(response.body().byteStream());
                        callBack.onResponse(null,true);
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.getException();
                    }
                } else {
                    System.out.println(response.body().string());

                    callBack.onResponse(null, false);
                }
            }
        });

    }
}
