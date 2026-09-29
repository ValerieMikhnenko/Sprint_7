package org.example.scooter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.DisplayName;

class CourierCreateTest extends BaseTest {

    private int courierId;

    static Stream<String> invalidCourierData() {
        return Stream.of(
                "{\"password\":\"123456\",\"firstName\":\"Valeria\"}",
                "{\"login\":\"test_valeria\",\"firstName\":\"Valeria\"}",
                "{\"firstName\":\"Valeria\"}"
        );
    }

    private void createCourier(Courier courier) {
        given()
                .spec(REQUEST_SPECIFICATION)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @AfterEach
    void deleteCourier() {
        if (courierId != 0) {
            given()
                    .spec(REQUEST_SPECIFICATION)
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200)
                    .body("ok", equalTo(true));
        }
    }

    @Test
    @DisplayName("Создание нового курьера")
    void createCourier() {

        Courier courier = new Courier(
                "test_valeria_create",
                "123456",
                "Valeria"
        );

        // Создаём курьера
        createCourier(courier);

        // Получаем ID созданного курьера
        // Получаем ID созданного курьера
        courierId = given()
                .spec(REQUEST_SPECIFICATION)
                .body(new CourierLoginRequest(
                        courier.getLogin(),
                        courier.getPassword()
                ))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");


    }
    @Test
    @DisplayName("Нельзя создать двух курьеров с одинаковым логином")
    void cannotCreateDuplicateCourier() {

        Courier courier = new Courier(
                "test_valeria_duplicate_20260929",
                "123456",
                "Valeria"
        );

        // Создаём курьера
        createCourier(courier);

        // Получаем ID созданного курьера
        courierId = given()
                .spec(REQUEST_SPECIFICATION)
                .body(new CourierLoginRequest(
                        courier.getLogin(),
                        courier.getPassword()
                ))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        // Пытаемся создать курьера с тем же логином
        given()
                .spec(REQUEST_SPECIFICATION)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("code", equalTo(409));

    }
    @ParameterizedTest
    @MethodSource("invalidCourierData")
    @DisplayName("Регистрация с некорректными данными")
    void cannotCreateCourierWithoutRequiredFields(String requestBody) {

        given()
                .spec(REQUEST_SPECIFICATION)
                .body(requestBody)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));
    }
}