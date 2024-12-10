package coms309.backEnd.demo.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDTO {
    private String name; // City name
    private Main main;   // Nested main object for temperature
    private Weather[] weather; // Array for weather descriptions

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    // Inner classes for nested fields
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private double temp;
        private double feels_like;

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getFeels_like() {
            return feels_like;
        }

        public void setFeels_like(double feels_like) {
            this.feels_like = feels_like;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}