import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class CourierLoginTest {
    private Courier courier;
    private CourierAccount courierAccount;
    private int courierId;


    @Before
    public void setUp() {
        courier = OrderFactory.getRandom();
        courierAccount = new CourierAccount();
        courierAccount.create(courier);
    }

    @After
    public void tearDown() {
        courierAccount.delete(courierId);
    }

    @Test
    @DisplayName("Авторизация успешна")
    @Description("/api/v1/courier/login")
    public void courierCanLoginValidTest() {
        ValidatableResponse loginResponse = courierAccount.login(CourierAuthData.from(courier));

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        courierId = loginResponse.extract().path("id");
        assertNotEquals(0, courierId);
    }

    @Test
    @DisplayName("Неудачная авторизация (пустой логин)")
    @Description("/api/v1/courier/login")
    public void courierLoginWithEmptyLoginTest() {
        ValidatableResponse loginResponse = courierAccount.login(new CourierAuthData("", courier.getPassword()));
        courierId = courierAccount.login(CourierAuthData.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Недостаточно данных для входа", bodyAnswer); // Проверка сообщения в теле ответа
    }

    @Test
    @DisplayName("Неудачная авторизация (пустой пароль)")
    @Description("/api/v1/courier/login")
    public void courierLoginWithEmptyPasswordTest() {
        ValidatableResponse loginResponse = courierAccount.login(new CourierAuthData(courier.getLogin(), ""));
        courierId = courierAccount.login(CourierAuthData.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Недостаточно данных для входа", bodyAnswer);
    }

    @Test
    @DisplayName("Неудачная авторизация (null логин)")
    @Description("/api/v1/courier/login")
    public void courierLoginWithNullLoginTest() {
        ValidatableResponse loginResponse = courierAccount.login(new CourierAuthData(null, courier.getPassword()));
        courierId = courierAccount.login(CourierAuthData.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Недостаточно данных для входа", bodyAnswer);
    }

    @Test
    @DisplayName("Неудачная авторизация (неверный логин)")
    @Description("/api/v1/courier/login")
    public void courierLoginWithIncorrectLoginTest() {
        ValidatableResponse loginResponse = courierAccount.login(new CourierAuthData("Test", courier.getPassword()));
        courierId = courierAccount.login(CourierAuthData.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_NOT_FOUND, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Учетная запись не найдена", bodyAnswer); // Проверка сообщения в теле ответа
    }

    @Test
    @DisplayName("Неудачная авторизация (неверный пароль)")
    @Description("/api/v1/courier/login")
    public void courierLoginWithIncorrectPasswordTest() {
        ValidatableResponse loginResponse = courierAccount.login(new CourierAuthData(courier.getLogin(), "Test"));
        courierId = courierAccount.login(CourierAuthData.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_NOT_FOUND, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Учетная запись не найдена", bodyAnswer);
    }

    @Test
    @DisplayName("Неудачная авторизация (несуществующий пользователь)")
    @Description("/api/v1/courier/login")
    public void loginWitNotExistLoginTest() {
        ValidatableResponse loginResponse = courierAccount.login(new CourierAuthData("Evklid", "Qwerty123$"));

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_NOT_FOUND, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Учетная запись не найдена", bodyAnswer);
    }
}