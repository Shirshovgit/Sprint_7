import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class BaseTest {

    public String pathCreateCourier = "/api/v1/courier";
    public String pathLoginCourier = "/api/v1/courier/login";

    public String pathFileUserFields = "src/test/resources/courier_create.json";

    public String pathOrder = "/api/v1/orders";

    public String successResponseNotFoundUser = "Учетная запись не найдена";

    public String successResponseNotEnoughData = "Недостаточно данных для входа";

    enum statusCode {
        SUCCESS_200(200),
        SUCCESS_201(201),
        FAILED_404(404),

        FAILED_400(400),
        FAILED_409(409);
        final int code;

        statusCode(int code) {
            this.code = code;
        }

    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    public Response sendPostRequest(String pathRequest, String pathFileBody) {
        step("Отправляем Post в ручку " + pathRequest);
        {
            File json = new File(pathFileBody);
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .body(json)
                    .post(pathRequest);
            return response;
        }
    }

    public Response sendPutRequest(String pathRequest, String queryParam, String queryValue) {
        step("Отправляем Put в ручку " + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .queryParam(queryParam, queryValue)
                    .when()
                    .put(pathRequest);
            return response;
        }
    }

    public Response sendPostRequestWithBody(String pathRequest, String body) {
        step("Отправляем Post в ручку" + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .body(body)
                    .post(pathRequest);
            return response;
        }
    }

    public Response sendGetRequest(String pathRequest) {
        step("Отправляем Get в ручку" + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .get(pathRequest);
            return response;
        }
    }

    public Response sendDeleteRequest(String pathRequest) {
        step("Отправляем Delete в ручку" + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .delete(pathRequest);
            return response;
        }
    }

    public Response sendDeleteWithBodyRequest(String pathRequest, String body) {
        step("Отправляем Delete в ручку" + pathRequest);
        {
            Response response = given().auth().none()
                    .header("Content-type", "application/json")
                    .body(body)
                    .delete(pathRequest);
            return response;
        }
    }

    @Step("Сравниваем статус кода ответа")
    public void compareStatusCodeResponse(Response response, Integer code) {
        Assert.assertTrue("Проверка завершилось ошибкой " + response.jsonPath().get().toString(),
                response.thenReturn().statusCode() == code);
    }

    @Step("Сравниваем тело ответа")
    public void compareBodyResponse(Response response, String parameterName, String messageText) {
        response.then().assertThat().body(parameterName, equalTo(messageText));
    }

    @Step("Сравниваем тело ответа")
    public void compareBodyResponse(Response response, String parameterName, boolean value) {
        response.then().assertThat().body(parameterName, equalTo(value));
    }

    @Step("Получаем ID курьрера в системе заказов")
    public String getIdCourier(Response response) {
        return response.jsonPath().get("id").toString();
    }
}
