package org.example.scooter;

import io.restassured.RestAssured;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

class OrderCreateTest {

    private static final String BASE_URL =
            "https://qa-scooter.education-services.ru";

    static Stream<String> orderColors() {
        return Stream.of(
                "BLACK",
                "GREY",
                "BLACK,GREY",
                ""
        );
    }

    @ParameterizedTest
    @MethodSource("orderColors")
    void createOrder(String colors) {

        RestAssured.baseURI = BASE_URL;

        String requestBody = "{"
                + "\"firstName\":\"Valeria\","
                + "\"lastName\":\"Test\","
                + "\"address\":\"Москва, ул. Тестовая, 1\","
                + "\"metroStation\":1,"
                + "\"phone\":\"+79991234567\","
                + "\"rentTime\":1,"
                + "\"deliveryDate\":\"2026-09-25\","
                + "\"comment\":\"Test order\""
                + "}";

        if (!colors.isEmpty()) {
            String[] colorArray = colors.split(",");

            requestBody = requestBody.replace(
                    "}",
                    String.format(
                            ", \"color\": [\"%s\"]}",
                            String.join("\", \"", colorArray)
                    )
            );
        }

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
