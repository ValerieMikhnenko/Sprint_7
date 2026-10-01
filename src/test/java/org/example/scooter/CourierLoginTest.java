package org.example.scooter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

class CourierLoginTest extends BaseTest {

    private int courierId;

    static Stream<String> invalidLoginFields() {
        return Stream.of(
                "{\"password\":\"123456\"}"
        );
    }

    private void createCourier(Courier courier) {
        given()
                .spec(REQUEST_SPECIFICATION)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @AfterEach
    void deleteCourier() {
        if (courierId != 0) {
            given()
                    .spec(REQUEST_SPECIFICATION)
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    void courierCanLogin() {

        Courier courier = new Courier(
                "test_valeria_login_20260928",
                "123456",
                "Valeria"
        );

        // Создаём курьера
        createCourier(courier);

        // Авторизуемся
        CourierLoginRequest loginRequest =
                new CourierLoginRequest(courier.getLogin(), courier.getPassword());

        courierId = given()
                .spec(REQUEST_SPECIFICATION)
                .body(loginRequest)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");


    }

    @Test
    @DisplayName("Нельзя войти с несуществующим логином")
    void cannotLoginWithWrongLogin() {

        given()
                .spec(REQUEST_SPECIFICATION)
                .body(new CourierLoginRequest("test_valeria_wrong", "123456"))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    @Test
    @DisplayName("Нельзя войти с неправильным паролем")
    void cannotLoginWithWrongPassword() {

        Courier courier = new Courier(
                "test_valeria_wrong_password_20260928",
                "123456",
                "Valeria"
        );

        // Создаём курьера
        createCourier(courier);

        // Пытаемся войти с неправильным паролем
        CourierLoginRequest loginRequest =
                new CourierLoginRequest(courier.getLogin(), "wrong_password");

        given()
                .spec(REQUEST_SPECIFICATION)
                .body(loginRequest)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));

// Получаем ID курьера
        loginRequest = new CourierLoginRequest(
                courier.getLogin(),
                courier.getPassword()
        );

        courierId = given()
                .spec(REQUEST_SPECIFICATION)
                .body(loginRequest)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");


    }

    @ParameterizedTest
    @MethodSource("invalidLoginFields")
    @DisplayName("Нельзя войти без обязательных полей")
    void cannotLoginWithoutRequiredFields(String requestBody) {

        given()
                .spec(REQUEST_SPECIFICATION)
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));
    }
}
