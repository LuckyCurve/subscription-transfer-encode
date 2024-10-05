package space.luckycurve.util.testing.performance;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

@Slf4j
class ExecutorTest {

    @Test
    void testExecute() {
        RestClient restClient = RestClient.builder().build();

        Response response = new Executor(new Request(1, 5, 4, () -> {
            restClient.get().uri("https://www.baidu.com/").retrieve().body(String.class);
            log.info("execute task complete");
        })).execute();

        Assertions.assertEquals(response.getTimeConsumingList().size(), 10);
    }
}