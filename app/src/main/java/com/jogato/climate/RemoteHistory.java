package com.jogato.climate;


import java.util.Map;


public class RemoteHistory {
    private String mWeatherURL;
    private Map<String, String> historyTimeStamp;

    public RemoteHistory(String weatherURL, Map<String, String>timeStamp){
        mWeatherURL = weatherURL;
        historyTimeStamp = timeStamp;
    }

    public String getmWeatherURL() {
        return mWeatherURL;
    }

    public Map<String, String> getHistoryTimeStamp() {
        return historyTimeStamp;
    }
}
