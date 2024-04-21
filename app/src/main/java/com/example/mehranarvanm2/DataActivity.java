package com.example.mehranarvanm2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.example.mehranarvanm2.databinding.DataActivityBinding;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataActivity extends AppCompatActivity {

    DataActivityBinding binding;


    public static int getImage(String code) {
        switch (code) {
            case "101":
                return R.drawable.img_1;
            case "1":
                return R.drawable.img;
            case "2":
            case "3":
                return R.drawable.img_2;
            case "102":
            case "103":
                return R.drawable.img_3;
            case "11":
            case "12":
            case "4":
                return R.drawable.img_4;
            case "111":
            case "112":
            case "104":
                return R.drawable.imm;
            case "5":
            case "6":
            case "8":
            case "9":
            case "10":
                return R.drawable.img_5;
            case "14":
            case "114":
                return R.drawable.img_7;
            case "13":
            case "113":
            case "7":
            case "107":
            case "115":
            case "15":
                return R.drawable.img_8;
            case "105":
            case "106":
            case "108":
            case "109":
                return R.drawable.img_6;
            default:
                return R.drawable.img;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataActivityBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        getData();
    }


    private int getIconWind(float wind) {

        if (wind <= 13.8f) return R.drawable.arrow_small;
        else if (wind <= 24.4) return R.drawable.arrow;
        else return R.drawable.arrow_large;
    }

    private double percentOfRange(long max, long min, long value) {
        long diff = max - min;
        long distance = value - min;
        return (double) distance / (double) diff;
    }


    @SuppressLint("DefaultLocale")
    private void initData() {

        List<String> weather_symbols = RepoNetwork.getInstance().getWeather("weather_symbol");
        List<String> temperatures = RepoNetwork.getInstance().getWeather("t_2m");
        List<String> temperatureDate = RepoNetwork.getInstance().getWeather("t_2m", true);
        List<String> wind_speeds = RepoNetwork.getInstance().getWeather("wind_speed");
        List<String> wind_dir = RepoNetwork.getInstance().getWeather("wind_dir");


        float temp = ((Float.parseFloat(temperatures.get(0)) - 32) / 1.8f);

        float max_temp = Float.parseFloat(RepoNetwork.getInstance().getWeather("t_max").get(0));


        String sunsetStr = RepoNetwork.getInstance().getWeather("sunset").get(0);
        String sunriseStr = RepoNetwork.getInstance().getWeather("sunrise").get(0);

        SimpleDateFormat utc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        utc.setTimeZone(TimeZone.getTimeZone("UTC"));
        long sunset = 0;
        long sunrise = 0;
        try {
            sunset = utc.parse(sunsetStr).getTime();
            sunrise = utc.parse(sunriseStr).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        double percent = percentOfRange(sunset, sunrise, System.currentTimeMillis());

        float min_temp = Float.parseFloat(RepoNetwork.getInstance().getWeather("t_min").get(0));

        float wind_speed = (Float.parseFloat(wind_speeds.get(0)) * 3.6f);

        List<String> precips = RepoNetwork.getInstance().getWeather("precip");
        float precip = Float.parseFloat(precips.get(0));
        ;
        float max = 0;
        for (int i = 0; i < precips.size(); i++)
            if (Float.parseFloat(precips.get(i)) > max) max = Float.parseFloat(precips.get(i));


        precip = precip * 100 / max;

        float uv = Float.parseFloat(RepoNetwork.getInstance().getWeather("uv").get(0));

        List<DataModel> dataModels = new ArrayList<>();

        for (int i = 0; i < temperatures.size(); i++) {
            dataModels.add(new DataModel(Float.parseFloat(temperatures.get(i)), temperatureDate.get(i), weather_symbols.get(i)));
        }


        try {
            int finalPrecip = (int) precip;
            runOnUiThread(() -> {
                binding.progress.setVisibility(View.GONE);
                binding.data.setVisibility(View.VISIBLE);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM, d");

                ItemAdapter itemAdapter = new ItemAdapter(dataModels);
                binding.rec.setAdapter(itemAdapter);
                LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
                linearSnapHelper.attachToRecyclerView(binding.rec);
                binding.rec.setLayoutManager(new LinearLayoutManager(DataActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.image.setImageResource(getImage(weather_symbols.get(0)));
                binding.temperature.setText(String.format("%.0fº", temp));
                binding.minMax.setText(String.format("Precipitations\nMax.: %.0fº   Min.: %.0fº", max_temp, min_temp));
                binding.wind.setText(String.format("%.0f km/h", wind_speed));
                binding.precip.setText(finalPrecip + " %");
                binding.today.setText(simpleDateFormat.format(new Date()));
                binding.uv.setText(String.format("%.0f", uv));
                binding.iconWind.setImageResource(getIconWind(Float.parseFloat(wind_speeds.get(0))));
                binding.iconWind.setRotation(Float.parseFloat(wind_dir.get(0)));
                binding.sunset.setProgress((float) percent);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* float f = Float.parseFloat(weather_symbol.get(0));
        float c = ((f - 32) / 1.8f);
        try {
            runOnUiThread(() -> { bn
                binding.c.setText(c + "");
            });
        } catch (Exception e) {
        }*/
    }

    private void getData() {
        RepoNetwork.getInstance().getData((data, isSuccess) -> {
            if (isSuccess) {
                initData();
            }
        });
    }
}
