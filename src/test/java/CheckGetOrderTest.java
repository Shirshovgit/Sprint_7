import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Test;

public class CheckGetOrderTest extends BaseTest {

    //Получить заказ по его номеру
    //Проверь:
    //успешный запрос возвращает объект с заказом;
    //запрос без номера заказа возвращает ошибку; +
    //запрос с несуществующим заказом возвращает ошибку.

    private String pathAcceptOrder = "/api/v1/orders/accept/";

    private String pathTrackOrder = "api/v1/orders/track?t=";

    private String messageFailGetInfoOrder = "Недостаточно данных для поиска";

    private String messageFailedGetInfoOrderWithIncorrectTrackId = "Заказ не найден";

    private String numberOrders;

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
    @DisplayName("Получение заказа")
    @Description("Провреяем, что при запросе заказа по id возвращается объект с информацией о заказе")
    public void shouldGetSuccessOrder() {

        Response createOrder = sendPostRequestWithBody(pathOrder, bodyParameterRequest);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);
        numberOrders = createOrder.jsonPath().get("track").toString();

        Response getIdOrders = sendGetRequest(pathTrackOrder + numberOrders);
        compareStatusCodeResponse(getIdOrders, statusCode.SUCCESS_200.code);
        System.out.println(getIdOrders.jsonPath().get().toString());
        compareBodyResponse(getIdOrders, "firstName", "Naruto");
    }

    @Test
    @DisplayName("Получение заказа")
    @Description("Провреяем, что при запросе заказа без id возвращается ошибка")
    public void shouldGetInfoOrderWithOutIdTrack() {

        Response createOrder = sendPostRequestWithBody(pathOrder, bodyParameterRequest);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);

        Response getIdOrders = sendGetRequest(pathTrackOrder);
        compareStatusCodeResponse(getIdOrders, statusCode.FAILED_400.code);
        compareBodyResponse(getIdOrders, "message", messageFailGetInfoOrder);
    }

    @Test
    @DisplayName("Получение заказа")
    @Description("Провреяем, что при запросе c некорректным id заказа - возвращается ошибка")
    public void shouldGetInfoOrderWithIncorrectTrack() {

        Response createOrder = sendPostRequestWithBody(pathOrder, bodyParameterRequest);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);

        Response getIdOrders = sendGetRequest(pathTrackOrder + "1");
        compareStatusCodeResponse(getIdOrders, statusCode.FAILED_404.code);
        compareBodyResponse(getIdOrders, "message", messageFailedGetInfoOrderWithIncorrectTrackId);
    }
}
