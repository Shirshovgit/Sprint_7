import io.qameta.allure.Step;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import java.util.Arrays;
import java.util.List;

public class CheckCourierCreateTest extends BaseTest {

    private String successResponseLoginUsed = "Этот логин уже используется";

    private String getSuccessResponseLowParameter = "Недостаточно данных для создания учетной записи";

    private List<String> bodyRequestCreateCourier = Arrays.asList("{\n" +
            "    \"password\": \"1234\",\n" +
            "    \"firstName\": \"saske\"\n" +
            "}", "{\n" +
            "    \"login\": \"ninja\",\n" +
            "    \"firstName\": \"saske\"\n" +
            "}");

    @Test
    @DisplayName("Создание курьера")
    @Description("Провреяем, что можно успешно создать нового курьера")
    public void createCourierStatusCodeCheck() {
        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok", true);
        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);
        Response deleteCourier = sendDeleteRequest(pathCreateCourier + "/" + getIdCourier(checkLogin));
        compareStatusCodeResponse(deleteCourier, statusCode.SUCCESS_200.code);
        compareBodyResponse(deleteCourier, "ok", true);
        Response checkLoginAfterDelete = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLoginAfterDelete, statusCode.FAILED_404.code);
        compareBodyResponse(checkLoginAfterDelete, "message", successResponseNotFoundUser);
    }

    @Test
    @DisplayName("Создание дублирующего курьера")
    @Description("Провреяем, что нельзя создать одного курьера два раза")
    public void createRepeatedCourierFailed() {
        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, 201);
        compareBodyResponse(createCourier, "ok", true);
        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);
        Response createRepeatedCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createRepeatedCourier, statusCode.FAILED_409.code);
        compareBodyResponse(createRepeatedCourier, "message", successResponseLoginUsed);
        Response deleteCourier = sendDeleteRequest(pathCreateCourier + "/" + getIdCourier(checkLogin));
        compareStatusCodeResponse(deleteCourier, statusCode.SUCCESS_200.code);
        compareBodyResponse(deleteCourier, "ok", true);
        Response checkLoginAfterDelete = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLoginAfterDelete, statusCode.FAILED_404.code);
        compareBodyResponse(checkLoginAfterDelete, "message", successResponseNotFoundUser);
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Провреяем, что для создания курьера нужны все обязательные поля")
    public void checkRequiredParameterForCreateCourier() {
        for (String body : bodyRequestCreateCourier) {
            Response createCourier = sendPostRequestWithBody(pathCreateCourier, body);
            compareStatusCodeResponse(createCourier, statusCode.FAILED_400.code);
            compareBodyResponse(createCourier, "message", getSuccessResponseLowParameter);
        }
    }

    @After
    @Step("Удаляем курьера по завершению теста")
    public void deleteCourier() {
        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        if (checkLogin.statusCode() == statusCode.SUCCESS_200.code) {
            Response deleteCourier = sendDeleteRequest(pathCreateCourier + "/" + getIdCourier(checkLogin));
            compareStatusCodeResponse(deleteCourier, statusCode.SUCCESS_200.code);
            compareBodyResponse(deleteCourier, "ok", true);
            Response checkLoginAfterDelete = sendPostRequest(pathLoginCourier, pathFileUserFields);
            compareStatusCodeResponse(checkLoginAfterDelete, statusCode.FAILED_404.code);
            compareBodyResponse(checkLoginAfterDelete, "message", successResponseNotFoundUser);
        }
    }

}
