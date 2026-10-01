package org.example.scooter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.DisplayName;

class CourierCreateTest extends BaseTest {

    private final CourierClient courierClient = new CourierClient();

    private int courierId;

    static Stream<String> invalidCourierData() {
        return Stream.of(
                "{\"password\":\"123456\",\"firstName\":\"Valeria\"}",
                "{\"login\":\"test_valeria\",\"firstName\":\"Valeria\"}",
                "{\"firstName\":\"Valeria\"}"
        );
    }

    @AfterEach
    void deleteCourier() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId)
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
        Response createResponse = courierClient.createCourier(courier);

        createResponse.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // Получаем ID созданного курьера
        Response loginResponse = courierClient.loginCourier(
                new CourierLoginRequest(
                        courier.getLogin(),
                        courier.getPassword()
                )
        );

        courierId = loginResponse.then()
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
        Response createResponse = courierClient.createCourier(courier);

        createResponse.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // Получаем ID созданного курьера
        Response loginResponse = courierClient.loginCourier(
                new CourierLoginRequest(
                        courier.getLogin(),
                        courier.getPassword()
                )
        );

        courierId = loginResponse.then()
                .statusCode(200)
                .extract()
                .path("id");

        // Пытаемся создать курьера с тем же логином
        Response duplicateResponse = courierClient.createCourier(courier);

        duplicateResponse.then()
                .statusCode(409)
                .body("code", equalTo(409));

    }
    @ParameterizedTest
    @MethodSource("invalidCourierData")
    @DisplayName("Регистрация с некорректными данными")
    void cannotCreateCourierWithoutRequiredFields(String requestBody) {

        Response response = courierClient.createCourier(requestBody);

        response.then()
                .statusCode(400)
                .body("code", equalTo(400));
    }
}