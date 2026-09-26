package org.example.scooter;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CourierCreateTest {

    private static final String BASE_URL =
            "https://qa-scooter.education-services.ru";
    static Stream<String> invalidCourierData() {
        return Stream.of(
                "{\"password\":\"123456\",\"firstName\":\"Valeria\"}",
                "{\"login\":\"test_valeria\",\"firstName\":\"Valeria\"}",
                "{\"firstName\":\"Valeria\"}"
        );
    }

    @Test
    void createCourier() {

        RestAssured.baseURI = BASE_URL;

        String login = "test_valeria_create";
        String password = "123456";
        String firstName = "Valeria";

        // Создаём курьера
        given()
                .header("Content-type", "application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                        login,
                        password,
                        firstName
                ))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // Получаем ID созданного курьера
        int courierId = given()
                .header("Content-type", "application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\"}",
                        login,
                        password
                ))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        // Удаляем курьера
        given()
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }
    @Test
    void cannotCreateDuplicateCourier() {

        RestAssured.baseURI = BASE_URL;

        String login = "test_valeria_duplicate";
        String password = "123456";
        String firstName = "Valeria";

        // Создаём курьера
        given()
                .header("Content-type", "application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                        login,
                        password,
                        firstName
                ))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // Пытаемся создать курьера с тем же логином
        given()
                .header("Content-type", "application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                        login,
                        password,
                        firstName
                ))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("code", equalTo(409));

        // Находим ID курьера для удаления
        int courierId = given()
                .header("Content-type", "application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"%s\"}",
                        login,
                        password
                ))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        // Удаляем созданного курьера
        given()
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }
    @ParameterizedTest
    @MethodSource("invalidCourierData")
    void cannotCreateCourierWithoutRequiredFields(String requestBody) {

        RestAssured.baseURI = BASE_URL;

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));
    }
}