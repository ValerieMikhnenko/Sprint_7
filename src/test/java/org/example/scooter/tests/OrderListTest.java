package org.example.scooter.tests;

import io.restassured.response.Response;
import org.example.scooter.client.OrderClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

class OrderListTest extends BaseTest {

    private final OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получить список заказов")
    void getOrderList() {

        Response response = orderClient.getOrders();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
