package coms309.backEnd.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/weather")
@Tag(name = "Weather Controller", description = "Fetches current weather for Ames, IA")
public class WeatherController {

    @Value("${openweather.api.url}")
    private String apiUrl;

    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "Get current weather for Ames, IA", description = "Fetches current weather data for Ames, IA")
    @GetMapping
    public ResponseEntity<?> getWeatherForAmes() {
        try {
            // Construct the API URL
            String url = String.format("%s?q=Ames,IA,US&appid=%s&units=metric", apiUrl, apiKey);

            // Make the API request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Parse the JSON response
            JsonNode root = objectMapper.readTree(response.getBody());

            // Extract details
            String cityName = root.path("name").asText();
            JsonNode main = root.path("main");
            double temp = main.path("temp").asDouble();
            double feelsLike = main.path("feels_like").asDouble();
            double minTemp = main.path("temp_min").asDouble();
            double maxTemp = main.path("temp_max").asDouble();
            int humidity = main.path("humidity").asInt();
            double pressure = main.path("pressure").asDouble();

            JsonNode weatherNode = root.path("weather").get(0);
            String weatherDescription = weatherNode.path("description").asText();
            String icon = weatherNode.path("icon").asText();

            JsonNode wind = root.path("wind");
            double windSpeed = wind.path("speed").asDouble();
            double windDeg = wind.path("deg").asDouble();

            JsonNode sys = root.path("sys");
            String country = sys.path("country").asText();
            long sunrise = sys.path("sunrise").asLong();
            long sunset = sys.path("sunset").asLong();

            long timestamp = root.path("dt").asLong();

            // Build JSON response
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode weatherDetails = mapper.createObjectNode();
            weatherDetails.put("temperature", temp);
            weatherDetails.put("feels_like", feelsLike);
            weatherDetails.put("min_temperature", minTemp);
            weatherDetails.put("max_temperature", maxTemp);
            weatherDetails.put("humidity", humidity);
            weatherDetails.put("pressure", pressure);
            weatherDetails.put("description", weatherDescription);
            weatherDetails.put("icon", icon);
            weatherDetails.put("wind_speed", windSpeed);
            weatherDetails.put("wind_direction", windDeg);
            weatherDetails.put("sunrise", sunrise);
            weatherDetails.put("sunset", sunset);
            weatherDetails.put("timestamp", timestamp);

            // Return the hashmap as a json string with key - value pairs
            return ResponseEntity.ok(weatherDetails);

        } catch (Exception e) {
            // Handle errors
            return ResponseEntity.status(500).body("Error fetching weather data: " + e.getMessage());
        }
    }
}


