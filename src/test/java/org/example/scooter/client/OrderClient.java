package org.example.scooter.client;

import io.restassured.response.Response;
import org.example.scooter.tests.BaseTest;
import org.example.scooter.pojo.OrderCreateRequest;

import static io.restassured.RestAssured.given;
import io.qameta.allure.Step;

public class OrderClient {

    @Step("Создать заказ")
    public Response createOrder(OrderCreateRequest order) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Получить список заказов")
    public Response getOrders() {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .when()
                .get("/api/v1/orders");
    }
}