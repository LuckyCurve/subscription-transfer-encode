package space.luckycurve.subscribe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Subscribe {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlAndEncodeInfoMaps {
        private List<String> encodeUrl = new ArrayList<>();

        private Map<String, String> decodeInfo = new HashMap<>();
    }


}
