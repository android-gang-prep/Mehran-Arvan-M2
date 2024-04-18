package com.example.mehranarvanm2;

import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mehranarvanm2.databinding.DataActivityBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataActivity extends AppCompatActivity {

    DataActivityBinding binding;
    private OkHttpClient client;
    private String data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = new OkHttpClient();
        getData();
    }


    public List<String> parse(String name) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(data));
            parser.nextTag();
            return readFeed(parser, name);

        } finally {
        }
    }

    private List<String> readFeed(XmlPullParser parser, String n) throws XmlPullParserException, IOException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("data")) {
                return readData(parser, n);
            } else {
                skip(parser);
            }
        }
        return new ArrayList<>();
    }


    private List<String> readData(XmlPullParser parser, String n) throws XmlPullParserException, IOException {

        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            String value = parser.getAttributeValue(0);
            if (name.equals("parameter") && value.startsWith(n)) {
                return readParameter(parser);
            } else
                skip(parser);
        }
        return new ArrayList<>();
    }

    private List<String> readParameter(XmlPullParser parser) throws XmlPullParserException, IOException {

        List<String> strings = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("location")) {
                strings.add(readValue(parser));
            } else {
                skip(parser);
            }

        }
        return strings;
    }

    private String readValue(XmlPullParser parser) throws XmlPullParserException, IOException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("value")) {
                return readText(parser);
            }


        }
        return "";
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            return;
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private void initData() throws XmlPullParserException, IOException {
        List<String> strings = parse("t_2m");
        float f = Float.parseFloat(strings.get(0));
        float c = ((f - 32) / 1.8f);
        try {
            runOnUiThread(() -> {
                binding.c.setText(c+"");
            });
        } catch (Exception e) {
        }
    }

    private void getData() {
        Request.Builder builder = new Request.Builder();
        String base64 = java.util.Base64.getEncoder().encodeToString("cloud_maghari_setare:0NI7zP87ou".getBytes());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = dateFormat.format(new Date());
        Log.i("TAG", "getData: " + date);
        builder.url("http://api.meteomatics.com/" + date + "ZP1D:PT1H/wind_speed_10m:ms,wind_dir_10m:d,t_2m:F,weather_symbol_1h:idx,precip_24h:mm,uv:idx,sunset:sql/35.721324,51.342037/xml");
        builder.addHeader("Authorization", "Basic " + base64);

        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("TAG", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        data = response.body().string();
                        initData();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("TAG", "onFailure: " + response.body().string());

                }
            }
        });
    }
}
