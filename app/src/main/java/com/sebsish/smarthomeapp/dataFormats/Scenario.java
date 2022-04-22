package com.sebsish.smarthomeapp.dataFormats;

public class Scenario {
    private int position;

    private String condition;
    private int threshold_temperature;

    private Boolean action;
    private Boolean enabled;

    private String indicator;
    private String socket_module;
    private String temperature_module;

    public int getPosition() {
        return position;
    }
    public String getIndicator() {
        return indicator;
    }
    public String getCondition() {
        return condition;
    }
    public int getThreshold_temperature() {
        return threshold_temperature;
    }
    public Boolean getAction() {
        return action;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public String getSocket_module() {
        return socket_module;
    }
    public String getTemperature_module() {
        return temperature_module;
    }

    public Scenario() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Scenario(String condition, String indicator, String socket_module, String temperature_module,int threshold_temperature, int position, Boolean action, Boolean enabled) {
        this.condition = condition;
        this.indicator = indicator;
        this.socket_module = socket_module;
        this.temperature_module = temperature_module;
        this.threshold_temperature = threshold_temperature;
        this.position = position;
        this.action = action;
        this.enabled = enabled;
    }
}

