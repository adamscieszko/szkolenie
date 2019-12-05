package com.example;

import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.example.SystemTestEnvironment.serviceUrl;
import static com.example.SystemTestEnvironment.start;
import static com.example.SystemTestEnvironment.wireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.given;

public class WeatherTest {

    @BeforeClass
    public static void startEnvironment() {
        start();
    }

    @Test
    public void should_get_city_and_weather() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        givenWeather("poland", "gdansk", 18);
        givenWeather("poland", "gdynia", 16);

        given()
            .baseUri(serviceUrl())
        .when()
            .get("/api/POL")
        .then()
            .body("", IsCollectionWithSize.hasSize(2))
            .body("[0].city.name", CoreMatchers.equalTo("Gdansk"))
            .body("[0].temperature", CoreMatchers.equalTo(18))
            .statusCode(200);
    }

    private void givenWeather(String country, String city, Integer temp) {
        wireMock().register(get(urlPathEqualTo("/" + country + "/" + city))
                .withHeader("host", equalTo("weather:8080"))
                .willReturn(aResponse().withBody(String.valueOf(temp)))
        );
    }
}
