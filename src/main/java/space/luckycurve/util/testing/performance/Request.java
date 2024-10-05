package space.luckycurve.util.testing.performance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class Request {

    private Integer warmUpNumber;

    private Integer parallel;

    private Integer requestPerThreadNumber;

    private Runnable task;
}
