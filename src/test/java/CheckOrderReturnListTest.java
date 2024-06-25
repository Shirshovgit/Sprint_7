import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class CheckOrderReturnListTest extends BaseTest {
    @Test
    @DisplayName("Список заказов")
    @Description("Провреяем, список заказов")
    public void shouldReturnNotFoundWithAuthorizationNoCreateUser() {
        Response checkOrderList = sendGetRequest(pathOrder + "?limit=1&page=0");
        compareStatusCodeResponse(checkOrderList, statusCode.SUCCESS_200.code);
        checkOrderList.then().assertThat().body("orders", notNullValue());
    }
}
