import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateCourierTest {
    private Courier courier;
    private CourierAccount courierClient;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierAccount();
    }

    @After
    public void tearDown() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("/api/v1/courier")
    public void courierCreatedTest() {
        courier = OrderFactory.getRandom();
        ValidatableResponse response = courierClient.create(courier);
        ValidatableResponse loginResponse = courierClient.login(CourierAuthData.from(courier));

        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, loginStatusCode);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_CREATED, statusCode);

        boolean isCreated = response.extract().path("ok");
        assertTrue(isCreated);

        courierId = loginResponse.extract().path("id");
        assertNotEquals(0, courierId);
    }

    @Test
    @DisplayName("Неудачное создание уже существующего курьера")
    @Description("/api/v1/courier")
    public void createTwoIdenticalCouriersTest() {
        Courier courierTEST = new Courier(OrderFactory.getRandom().getName(),
                OrderFactory.getRandom().getLogin(),
                OrderFactory.getRandom().getPassword());
        courierClient.create(courierTEST);
        ValidatableResponse response = courierClient.create(courierTEST);
        ValidatableResponse loginResponse = courierClient.login(CourierAuthData.from(courierTEST));
        courierId = loginResponse.extract().path("id");

        String bodyAnswer = response.extract().path("message");
        assertEquals("Этот логин уже используется. Попробуйте другой.", bodyAnswer);

        int StatusCode = response.extract().statusCode();
        assertEquals(SC_CONFLICT, StatusCode);
    }

    @Test
    @DisplayName("Неудачное создание курьера с паролем null")
    @Description("/api/v1/courier")
    public void createCourierWithNullPasswordTest() {
        Courier courierTEST = new Courier(OrderFactory.getRandom().getName(),
                OrderFactory.getRandom().getLogin(),
                null);
        ValidatableResponse response = courierClient.create(courierTEST);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = response.extract().path("message");
        assertEquals("Недостаточно данных для создания учетной записи", bodyAnswer);
    }

    @Test
    @DisplayName("Неудачное создание курьера с именем null")
    @Description("/api/v1/courier")
    public void courierCreateWithNullFirstNameTest() {
        Courier courierTEST = new Courier(null,
                OrderFactory.getRandom().getLogin(),
                OrderFactory.getRandom().getPassword());
        ValidatableResponse response = courierClient.create(courierTEST);
        courierId = courierClient.login(CourierAuthData.from(courierTEST)).extract().path("id");

        int statusCode = response.extract().statusCode();
        assertEquals(SC_CREATED, statusCode);

        boolean isCreated = response.extract().path("ok");
        assertTrue(isCreated);
    }

    @Test
    @DisplayName("Неудачное создание курьера с пустым логином")
    @Description("/api/v1/courier")
    public void createCourierWithEmptyLoginTest() {
        Courier courierTEST = new Courier(OrderFactory.getRandom().getName(),
                "",
                OrderFactory.getRandom().getPassword());
        ValidatableResponse response = courierClient.create(courierTEST);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = response.extract().path("message");
        assertEquals("Недостаточно данных для создания учетной записи", bodyAnswer);
    }

    @Test
    @DisplayName("Неудачное создание курьера с пустым паролем")
    @Description("/api/v1/courier")
    public void createCourierWithEmptyPasswordTest() {
        Courier courierTEST = new Courier(OrderFactory.getRandom().getName(),
                OrderFactory.getRandom().getLogin(),
                "");
        ValidatableResponse response = courierClient.create(courierTEST);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = response.extract().path("message");
        assertEquals("Недостаточно данных для создания учетной записи", bodyAnswer);
    }
}