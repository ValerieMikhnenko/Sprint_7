package org.example.scooter.tests;

import io.restassured.response.Response;
import org.example.scooter.client.CourierClient;
import org.example.scooter.pojo.Courier;
import org.example.scooter.pojo.CourierLoginRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

class CourierLoginTest extends BaseTest {

    private final CourierClient courierClient = new CourierClient();

    private int courierId;

    static Stream<String> invalidLoginFields() {
        return Stream.of(
                "{\"password\":\"123456\"}"
        );
    }

    @AfterEach
    void deleteCourier() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId)
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

        Response createResponse = courierClient.createCourier(courier);

        createResponse.then()
                .statusCode(201);

        CourierLoginRequest loginRequest =
                new CourierLoginRequest(courier.getLogin(), courier.getPassword());

        Response loginResponse = courierClient.loginCourier(loginRequest);

        courierId = loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Нельзя войти с несуществующим логином")
    void cannotLoginWithWrongLogin() {

        Response response = courierClient.loginCourier(
                new CourierLoginRequest("test_valeria_wrong", "123456")
        );

        response.then()
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

        Response createResponse = courierClient.createCourier(courier);

        createResponse.then()
                .statusCode(201);

        CourierLoginRequest loginRequest =
                new CourierLoginRequest(courier.getLogin(), "wrong_password");

        Response wrongPasswordResponse = courierClient.loginCourier(loginRequest);

        wrongPasswordResponse.then()
                .statusCode(404)
                .body("code", equalTo(404));

        loginRequest = new CourierLoginRequest(
                courier.getLogin(),
                courier.getPassword()
        );

        Response correctLoginResponse = courierClient.loginCourier(loginRequest);

        courierId = correctLoginResponse.then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @ParameterizedTest
    @MethodSource("invalidLoginFields")
    @DisplayName("Нельзя войти без обязательных полей")
    void cannotLoginWithoutRequiredFields(String requestBody) {

        Response response = courierClient.loginCourier(requestBody);

        response.then()
                .statusCode(400)
                .body("code", equalTo(400));
    }
}
