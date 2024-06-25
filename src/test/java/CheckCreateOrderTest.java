import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class CheckCreateOrderTest extends BaseTest {

    private final String body;

    public CheckCreateOrderTest(String body) {
        this.body = body;
    }

    @Parameterized.Parameters
    public static Object[] getBody() {
        return new Object[][]{
                {"{\n" +
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
                        "}"},
                {"{\n" +
                        "    \"firstName\": \"Naruto\",\n" +
                        "    \"lastName\": \"Uchiha\",\n" +
                        "    \"address\": \"Konoha, 142 apt.\",\n" +
                        "    \"metroStation\": 4,\n" +
                        "    \"phone\": \"+7 800 355 35 35\",\n" +
                        "    \"rentTime\": 5,\n" +
                        "    \"deliveryDate\": \"2020-06-06\",\n" +
                        "    \"comment\": \"Saske, come back to Konoha\",\n" +
                        "    \"color\": [\n" +
                        "        \"GRAY\"\n" +
                        "    ]\n" +
                        "}"},
                {"{\n" +
                        "  \"firstName\": \"Naruto\",\n" +
                        "  \"lastName\": \"Uchiha\",\n" +
                        "  \"address\": \"Konoha, 142 apt.\",\n" +
                        "  \"metroStation\": 4,\n" +
                        "  \"phone\": \"+7 800 355 35 35\",\n" +
                        "  \"rentTime\": 5,\n" +
                        "  \"deliveryDate\": \"2020-06-06\",\n" +
                        "  \"comment\": \"Saske, come back to Konoha\",\n" +
                        "  \"color\": [\n" +
                        "    \"BLACK\",\n" +
                        "    \"GRAY\"\n" +
                        "  ]\n" +
                        "}"},
                {"{\n" +
                        "    \"firstName\": \"Naruto\",\n" +
                        "    \"lastName\": \"Uchiha\",\n" +
                        "    \"address\": \"Konoha, 142 apt.\",\n" +
                        "    \"metroStation\": 4,\n" +
                        "    \"phone\": \"+7 800 355 35 35\",\n" +
                        "    \"rentTime\": 5,\n" +
                        "    \"deliveryDate\": \"2020-06-06\",\n" +
                        "    \"comment\": \"Saske, come back to Konoha\",\n" +
                        "    \"color\": [\n" +
                        "        \"\"\n" +
                        "    ]\n" +
                        "}"}
        };
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Провреяем, создание заказа с разными цветами")
    public void shouldReturnNotFoundWithAuthorizationNoCreateUser() {
        Response createOrder = sendPostRequestWithBody(pathOrder, body);
        compareStatusCodeResponse(createOrder, statusCode.SUCCESS_201.code);
        Assert.assertNotNull(createOrder.jsonPath().get("track").toString());
    }
}
