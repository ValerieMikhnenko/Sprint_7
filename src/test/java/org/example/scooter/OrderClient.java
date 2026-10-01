package org.example.scooter;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    public Response createOrder(OrderCreateRequest order) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    public Response getOrders() {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .when()
                .get("/api/v1/orders");
    }
}