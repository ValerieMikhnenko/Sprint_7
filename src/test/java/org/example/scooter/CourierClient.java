package org.example.scooter;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierClient {

    public Response createCourier(Courier courier) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    public Response loginCourier(CourierLoginRequest loginRequest) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(loginRequest)
                .when()
                .post("/api/v1/courier/login");
    }

    public Response deleteCourier(int courierId) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .when()
                .delete("/api/v1/courier/" + courierId);
    }

    public Response createCourier(String requestBody) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(requestBody)
                .when()
                .post("/api/v1/courier");
    }

    public Response loginCourier(String requestBody) {
        return given()
                .spec(BaseTest.REQUEST_SPECIFICATION)
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");
    }
}