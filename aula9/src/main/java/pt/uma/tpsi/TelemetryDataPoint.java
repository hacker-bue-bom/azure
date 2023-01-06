package pt.uma.tpsi;

import com.google.gson.Gson;

public class TelemetryDataPoint {
    public double temperature;
    public double humidity;

    public TelemetryDataPoint(double temperature, double humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
    }

    //serialize object to JSON
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
