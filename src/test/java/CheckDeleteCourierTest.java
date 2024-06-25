import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Test;

public class CheckDeleteCourierTest extends BaseTest {

    private String invalidCourierId = "0";

    private String bodyMessageFailedRequestDelete = "Курьера с таким id нет.";

    private String bodyFailedRequestWithOutIdCourierDelete = "Недостаточно данных для удаления курьера";

    @Test
    @DisplayName("Удаление курьера")
    @Description("Проверяем неуспешное удаление курьера")
    public void deleteFailedCourier() {
        Response deleteCourier = sendDeleteRequest(pathCreateCourier + "/" + invalidCourierId);
        compareStatusCodeResponse(deleteCourier, statusCode.FAILED_404.code);
        compareBodyResponse(deleteCourier, "message", bodyMessageFailedRequestDelete);
    }

    @Test
    @DisplayName("Удаление курьера")
    @Description("Проверяем успешное удаление курьера")
    public void deleteSuccessCourier() {
        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok", true);
        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);
        Response deleteCourier = sendDeleteRequest(pathCreateCourier + "/" + getIdCourier(checkLogin));
        compareStatusCodeResponse(deleteCourier, statusCode.SUCCESS_200.code);
        compareBodyResponse(deleteCourier, "ok", true);
    }

    @Test
    @DisplayName("Удаление курьера")
    @Description("Проверяем ошибку, если не был передал ID курьера в ручку при DELETE")
    public void deleteCourierWithOutCourierId() {
        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok", true);
        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);
        Response deleteCourier = sendDeleteWithBodyRequest(pathCreateCourier, "{\n" +
                "    \"id\": \"\"\n" +
                "}");
        compareStatusCodeResponse(deleteCourier, statusCode.FAILED_400.code);
        compareBodyResponse(deleteCourier, "message", bodyFailedRequestWithOutIdCourierDelete);
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
            compareBodyResponse(deleteCourier, "message", bodyMessageFailedRequestDelete);
        }
    }
}
