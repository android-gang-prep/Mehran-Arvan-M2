package com.example.mehranarvanm2;

public class DataModel {
    private float temp;
    private String date;
    private String weather_symbol;

    public DataModel(float temp, String date, String weather_symbol) {
        this.temp = temp;
        this.date = date;
        this.weather_symbol = weather_symbol;
    }


    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather_symbol() {
        return weather_symbol;
    }

    public void setWeather_symbol(String weather_symbol) {
        this.weather_symbol = weather_symbol;
    }
}
