package space.luckycurve.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Couple<F, S> {

    private F first;

    private S second;
}
