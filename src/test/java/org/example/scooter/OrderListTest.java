package org.example.scooter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

class OrderListTest extends BaseTest {

    @Test
    @DisplayName("Получить список заказов")
    void getOrderList() {

        given()
                .spec(REQUEST_SPECIFICATION)
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
