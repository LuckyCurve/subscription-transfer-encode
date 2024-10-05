package space.luckycurve.util.testing.performance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class Response {

    private final ArrayList<Long> timeConsumingList;

    @Override
    public String toString() {
        return "Response [p50 = " + getP50() + "ms, p90 = " + getP90() + "ms, p95 = " + getP95() + "ms]";
    }

    public Long getP50() {
        return timeConsumingList.getLast();
    }

    public Long getP90() {
        int index = (int) (totalCount() * 0.1);
        return timeConsumingList.get(index);
    }

    public Long getP95() {
        int index = (int) (totalCount() * 0.05);
        return timeConsumingList.get(index);
    }

    private Integer totalCount() {
        return timeConsumingList.size() * 2;
    }
}
