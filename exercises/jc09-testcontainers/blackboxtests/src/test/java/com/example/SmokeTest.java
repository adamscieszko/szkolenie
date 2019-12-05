package com.example;

import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.example.SystemTestEnvironment.serviceUrl;
import static com.example.SystemTestEnvironment.start;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class SmokeTest {

    @BeforeClass
    public static void startEnvironment() {
        start();
    }

    @Test
    public void healthIsUpx() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        given()
            .baseUri(serviceUrl())
        .when()
            .get("/health")
        .then()
            .body("status", equalTo("UP"))
            .statusCode(200);
    }
}
