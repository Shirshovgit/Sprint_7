import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Test;

public class CheckAcceptOrderTest extends BaseTest {

    private String pathAcceptOrder = "/api/v1/orders/accept/";

    private String pathTrackOrder = "api/v1/orders/track?t=";

    private String numberOrders;
    private String idOrders;

    private String bodyRequestFailNotFoundIdOrder = "Заказа с таким id не существует";

    private String bodyRequestFailNotFoundCourier = "Курьера с таким id не существует";

    private String getBodyRequestFailNotCourierId = "Недостаточно данных для поиска";

    private String bodyParameterRequest = "{\n" +
            "    \"firstName\": \"Naruto\",\n" +
            "    \"lastName\": \"Uchiha\",\n" +
            "    \"address\": \"Konoha, 142 apt.\",\n" +
            "    \"metroStation\": 4,\n" +
            "    \"phone\": \"+7 800 355 35 35\",\n" +
            "    \"rentTime\": 5,\n" +
            "    \"deliveryDate\": \"2020-06-06\",\n" +
            "    \"comment\": \"Saske, come back to Konoha\",\n" +
            "    \"color\": [\n" +
            "        \"BLACK\"\n" +
            "    ]\n" +
            "}";

    @Test
    @DisplayName("Принятие заказа")
    @Description("Провреяем, успешное принятия заказа")
    public void shouldSuccessAcceptOrder() {
        Response createOrder = sendPostRequestWithBody(pathOrder, bodyParameterRequest);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);
        numberOrders = createOrder.jsonPath().get("track").toString();

        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok",true);

        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);
        Response getIdOrders = sendGetRequest(pathTrackOrder + numberOrders);
        compareStatusCodeResponse(getIdOrders, statusCode.SUCCESS_200.code);
        idOrders = getIdOrders.jsonPath().get("order.id").toString();

        Response acceptOrder = sendPutRequest(pathAcceptOrder + idOrders, "courierId", getIdCourier(checkLogin));
        compareStatusCodeResponse(acceptOrder, statusCode.SUCCESS_200.code);
        compareBodyResponse(acceptOrder, "ok",true);
    }

    @Test
    @DisplayName("Принятие заказа")
    @Description("Провреяем, ошибки при отсутствии id заказа в параметрах")
    public void shouldFailWithOutIdOrderAcceptOrder() {
        Response createOrder = sendPostRequestWithBody(pathOrder, bodyParameterRequest);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);
        numberOrders = createOrder.jsonPath().get("track").toString();

        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok",true);

        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);
        Response getIdOrders = sendGetRequest(pathTrackOrder + numberOrders);
        compareStatusCodeResponse(getIdOrders, statusCode.SUCCESS_200.code);
        idOrders = getIdOrders.jsonPath().get("order.id").toString();

        Response acceptOrder = sendPutRequest(pathAcceptOrder, "courierId", getIdCourier(checkLogin));
        compareStatusCodeResponse(acceptOrder, statusCode.FAILED_400.code);
    }

    @Test
    @DisplayName("Принятие заказа")
    @Description("Провреяем ошибку при указании неверного id заказа в параметрах")
    public void shouldFailIncorrectOrderAcceptOrder() {
        Response createOrder = sendPostRequestWithBody(pathOrder, bodyParameterRequest);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);
        numberOrders = createOrder.jsonPath().get("track").toString();

        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok",true);

        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);

        Response acceptOrder = sendPutRequest(pathAcceptOrder + "0", "courierId", getIdCourier(checkLogin));
        compareStatusCodeResponse(acceptOrder, statusCode.FAILED_404.code);
        compareBodyResponse(acceptOrder, "message",bodyRequestFailNotFoundIdOrder);

    }

    @Test
    @DisplayName("Принятие заказа")
    @Description("Провреяем ошибку  если не передать id курьера в параметрах")
    public void shouldFailAcceptOrdedWithIncorrectCourierId() {
        Response createOrder = sendPostRequestWithBody(pathOrder, bodyParameterRequest);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);
        numberOrders = createOrder.jsonPath().get("track").toString();

        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok",true);

        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);

        Response acceptOrder = sendPutRequest(pathAcceptOrder + numberOrders, "", "");
        compareStatusCodeResponse(acceptOrder, statusCode.FAILED_400.code);
        compareBodyResponse(acceptOrder, "message",getBodyRequestFailNotCourierId);
    }

    @Test
    @DisplayName("Принятие заказа")
    @Description("Провреяем ошибку при указании неверного id курьрера в параметрах")
    public void shouldFailWitOutCourierIdAcceptOrder() {
        Response createOrder = sendPostRequestWithBody(pathOrder, bodyParameterRequest);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);
        numberOrders = createOrder.jsonPath().get("track").toString();

        Response createCourier = sendPostRequest(pathCreateCourier, pathFileUserFields);
        compareStatusCodeResponse(createCourier, statusCode.SUCCESS_201.code);
        compareBodyResponse(createCourier, "ok",true);

        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        compareStatusCodeResponse(checkLogin, statusCode.SUCCESS_200.code);

        Response acceptOrder = sendPutRequest(pathAcceptOrder + numberOrders, "courierId", "0");
        compareStatusCodeResponse(acceptOrder, statusCode.FAILED_404.code);
        compareBodyResponse(acceptOrder, "message",bodyRequestFailNotFoundCourier);
    }


    @After
    @Step("Удаляем курьера по завершению теста")
    public void deleteCourier() {
        Response checkLogin = sendPostRequest(pathLoginCourier, pathFileUserFields);
        if (checkLogin.statusCode() == statusCode.SUCCESS_200.code) {
            Response deleteCourier = sendDeleteRequest(pathCreateCourier + "/" + getIdCourier(checkLogin));
            compareStatusCodeResponse(deleteCourier, statusCode.SUCCESS_200.code);
            compareBodyResponse(deleteCourier,"ok", true);
            Response checkLoginAfterDelete = sendPostRequest(pathLoginCourier, pathFileUserFields);
            compareStatusCodeResponse(checkLoginAfterDelete, statusCode.FAILED_404.code);
            compareBodyResponse(checkLoginAfterDelete, "message",successResponseNotFoundUser);
        }
    }
}

