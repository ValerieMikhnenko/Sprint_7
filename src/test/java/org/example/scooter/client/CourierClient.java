package org.example.scooter.client;

import io.restassured.response.Response;
import org.example.scooter.tests.BaseTest;
import org.example.scooter.pojo.Courier;
import org.example.scooter.pojo.CourierLoginRequest;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

public class CourierClient {

    @Step("Создать курьера")
    public Response createCourier(Courier courier) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Авторизовать курьера")
    public Response loginCourier(CourierLoginRequest loginRequest) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(loginRequest)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удалить курьера с id {courierId}")
    public Response deleteCourier(int courierId) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .when()
                .delete("/api/v1/courier/" + courierId);
    }

    @Step("Создать курьера с невалидными данными")
    public Response createCourier(String requestBody) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(requestBody)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Авторизовать курьера с заданными данными")
    public Response loginCourier(String requestBody) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");
    }
}