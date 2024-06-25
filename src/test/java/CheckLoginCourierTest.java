import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CheckLoginCourierTest extends BaseTest {

    private List<String> bodyRequestLoginCourier = Arrays.asList("{\n" +
            "  \"login\": \"AlexShirshovv\",\n" +
            "  \"password\": \"\"\n" +
            "}", "{\n" +
            "  \"login\": \"\",\n" +
            "  \"password\": \"12344\"\n" +
            "}");

    private List<String> bodyRequestIncorrectLoginCourier = Arrays.asList("{\n" +
            "  \"login\": \"AlexShirshovv\",\n" +
            "  \"password\": \"1234\"\n" +
            "}", "{\n" +
            "  \"login\": \"AlexShirshov\",\n" +
            "  \"password\": \"12344\"\n" +
            "}");

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Провреяем, что курьер может авторизоваться")
    public void shouldLoginCourierSuccess() {
        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok", true);
        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);
        Assert.assertNotNull(getIdCourier(checkLogin));
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Провреяем, что возвращается ошибка при авторизации под несуществующим пользователем")
    public void shouldReturnNotFoundWithAuthorizationNoCreateUser() {
        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.FAILED_404.code);
        compareBodyResponse(checkLogin, "message", successResponseNotFoundUser);
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Провреяем, что при передаче только одного параметра в тело ручки авторизации вернется ошибка")
    public void shouldReturnFailedWithIncorrectBodyRequest() {
        for (String body : bodyRequestLoginCourier) {
            Response checkLogin = sendPostRequestWithBody(pathLoginCourier, body);
            compareStatusCodeResponse(checkLogin, statusCode.FAILED_400.code);
            compareBodyResponse(checkLogin, "message", successResponseNotEnoughData);
        }
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Провреяем, что при передаче неправильного логина/пароля - авторизация не происходит")
    public void shouldReturnFailedWithIncorrectLoginOrPasswordInRequest() {
        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok", true);
        for (String body : bodyRequestIncorrectLoginCourier) {
            Response checkLogin = sendPostRequestWithBody(pathLoginCourier, body);
            compareStatusCodeResponse(checkLogin, statusCode.FAILED_404.code);
            compareBodyResponse(checkLogin, "message", successResponseNotFoundUser);
        }
    }

    @After
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
