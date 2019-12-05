package com.example.testcontainers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

@RestController
class CitiesResource {

    private final CitiesRepository repository;
    private final WeatherClient weatherClient;

    CitiesResource(CitiesRepository repository, WeatherClient weatherClient) {
        this.repository = repository;
        this.weatherClient = weatherClient;
    }

    @GetMapping("/api/{country}")
    List<Weather> getTempInCountry(@PathVariable("country") String country) {
        return repository.getCitiesByCountry(country)
                .stream()
                .map(weatherClient::getWeather)
                .flatMap(o -> o.map(Stream::of).orElse(Stream.empty()))
                .collect(Collectors.toList());
    }

}

@Component
class WeatherClient {
    private final URI weatherUrl;

    WeatherClient(@Value("${service.weather}") URI weatherUrl) {
        this.weatherUrl = weatherUrl;
    }

    public Optional<Weather> getWeather(City city) {
        URI uri = UriComponentsBuilder.fromUri(weatherUrl)
                .pathSegment("{country}", "{city}")
                .buildAndExpand(city.getCountry().toLowerCase(), city.getName().toLowerCase()).toUri();

        try {
            String temp = new RestTemplate().getForObject(uri, String.class);
            return Optional.of(new Weather(city, Integer.valueOf(temp)));
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }
}

@Component
class CitiesRepository {

    private final JdbcTemplate jdbcTemplate;

    CitiesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<City> getCitiesByCountry(String country) {
        return jdbcTemplate.query("SELECT ci.Name as name, ci.Population as population, co.Name as country FROM " +
                "city ci, country co WHERE ci.CountryCode = co.Code AND co.Code = ?", new Object[] {country},
                (rs, i) -> new City(rs.getString("name"), rs.getString("country"), rs.getLong("population")));
    }
}

class Weather {
    private final City city;
    private final int temperature;

    Weather(City city, int temperature) {
        this.city = city;
        this.temperature = temperature;
    }

    public City getCity() {
        return city;
    }

    public int getTemperature() {
        return temperature;
    }
}

class City {
    private final String name;
    private final String country;
    private final Long population;


    City(String name, String country, Long population) {
        this.name = name;
        this.country = country;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Long getPopulation() {
        return population;
    }
}