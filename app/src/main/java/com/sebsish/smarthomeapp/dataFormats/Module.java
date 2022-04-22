package com.sebsish.smarthomeapp.dataFormats;

public class Module {
    private String type;
    private String name;


    private String room;
    private int position;

    private Boolean enabled;

    private int temperature;
    private int temperature_f;
    private int humidity;

    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public String getRoom() {
        return room;
    }
    public int getPosition() {
        return position;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public int getTemperature() {
        return temperature;
    }
    public int getTemperature_f() {
        return temperature_f;
    }
    public int getHumidity() {
        return humidity;
    }

    public Module() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Module(String type, String name, String room, int position, Boolean enabled) {
        this.type = type;
        this.name = name;
        this.room = room;
        this.position = position;
        this.enabled = enabled;
    }

    public Module(String type, String name, String room, int position, int temperature, int temperature_f, int humidity) {
        this.type = type;
        this.name = name;
        this.room = room;
        this.position = position;
        this.temperature = temperature;
        this.temperature_f = temperature_f;
        this.humidity = humidity;
    }
}

