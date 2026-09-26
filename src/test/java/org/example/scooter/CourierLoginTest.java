package org.example.scooter;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class CourierLoginTest {

    private static final String BASE_URL =
            "https://qa-scooter.education-services.ru";

    static Stream<String> invalidLoginFields() {
        return Stream.of(
                "{\"password\":\"123456\"}"
        );
    }

    @Test
    void courierCanLogin() {

        RestAssured.baseURI = BASE_URL;

        String login = "test_valeria_login";
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
                .statusCode(201);

        // Авторизуемся
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
                .body("id", notNullValue())
                .extract()
                .path("id");

        // Удаляем курьера
        given()
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @Test
    void cannotLoginWithWrongLogin() {

        RestAssured.baseURI = BASE_URL;

        given()
                .header("Content-type", "application/json")
                .body("{\"login\":\"test_valeria_wrong\",\"password\":\"123456\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    @Test
    void cannotLoginWithWrongPassword() {

        RestAssured.baseURI = BASE_URL;

        String login = "test_valeria_wrong_password";
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
                .statusCode(201);

        // Пытаемся войти с неправильным паролем
        given()
                .header("Content-type", "application/json")
                .body(String.format(
                        "{\"login\":\"%s\",\"password\":\"wrong_password\"}",
                        login
                ))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));

        // Получаем ID курьера
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
                .statusCode(200);
    }

    @ParameterizedTest
    @MethodSource("invalidLoginFields")
    void cannotLoginWithoutRequiredFields(String requestBody) {

        RestAssured.baseURI = BASE_URL;

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));
    }
}
