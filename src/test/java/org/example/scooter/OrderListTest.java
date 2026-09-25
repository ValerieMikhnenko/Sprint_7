package org.example.scooter;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

class OrderListTest {

    private static final String BASE_URL =
            "https://qa-scooter.education-services.ru";

    @Test
    void getOrderList() {
        RestAssured.baseURI = BASE_URL;

        given()
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
