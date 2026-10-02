package org.example.scooter.tests;

import io.restassured.response.Response;
import org.example.scooter.client.OrderClient;
import org.example.scooter.pojo.OrderCreateRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.DisplayName;

class OrderCreateTest extends BaseTest {

    private final OrderClient orderClient = new OrderClient();

    static Stream<String> orderColors() {
        return Stream.of(
                "BLACK",
                "GREY",
                "BLACK,GREY",
                ""
        );
    }

    @ParameterizedTest
    @MethodSource("orderColors")
    @DisplayName("Создание заказа")
    void createOrder(String colors) {

        List<String> colorList = colors.isEmpty()
                ? Collections.emptyList()
                : Arrays.asList(colors.split(","));

        OrderCreateRequest order = new OrderCreateRequest(
                "Valeria",
                "Test",
                "Москва, ул. Тестовая, 1",
                1,
                "+79991234567",
                1,
                "2026-09-25",
                "Test order",
                colorList
        );

        Response response = orderClient.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
