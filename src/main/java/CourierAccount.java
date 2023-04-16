import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class CourierAccount extends BaseSpecSettingsProvider {

    private static final String COURIER_PATH = "/api/v1/courier/";

    @Step("Логин курьера")
    public ValidatableResponse login(CourierAuthData credentials) {
        return given()
                .spec(getBaseSpecSettings())
                .body(credentials)
                .when()
                .post(COURIER_PATH + "login")
                .then();
    }

    @Step("Создание аккаунта нового курьера")
    public ValidatableResponse create(Courier courier) {
        return given()
                .spec(getBaseSpecSettings())
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then();
    }

    @Step("Удаление аккаунта курьера")
    public ValidatableResponse delete(int courierId) {
        return given()
                .spec(getBaseSpecSettings())
                .when()
                .delete(COURIER_PATH + courierId)
                .then();
    }
}