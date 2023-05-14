import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseSpecSettingsProvider {

    private static final String ORDER_CREATE_URL ="/api/v1/orders";

    @Step("Создание заказа")
    public ValidatableResponse create(Order order) {
        return given()
                .spec(getBaseSpecSettings())
                .body(order)
                .when()
                .post(ORDER_CREATE_URL)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getDefaultOrder() {
        return given()
                .spec(getBaseSpecSettings())
                .when()
                .get(ORDER_CREATE_URL)
                .then();
    }
}