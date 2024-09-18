package space.luckycurve.subscribe;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeService {

    @NonNull
    private final RestClient restClient;

    private final Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(10)
            .build();

    @SneakyThrows
    public String sub(String url, String key) {
        if (key != null) {
            url = cache.getIfPresent(key);
        }
        return doSub(url);
    }

    private String doSub(String url) {
        String resourceUrl = UriComponentsBuilder.fromUriString(url).build().getQueryParams().get("url").getFirst();
        List<String> urls = Arrays.asList(URLDecoder.decode(resourceUrl, StandardCharsets.UTF_8).split("\\|"));

        Map<String, String> encodeMap = urlEncodeWithUserInfo(urls);

        String actualRequestUrl = encodeUrl(url, encodeMap);

        log.info("actualRequestUrl: {}", actualRequestUrl);
        String response = restClient.get().uri(UriComponentsBuilder.fromUriString(actualRequestUrl).build(true).toUri()).retrieve().body(String.class);

        return decodeResponse(response, encodeMap);
    }

    private String decodeResponse(String response, Map<String, String> encodeMap) {
        for (Map.Entry<String, String> entry : encodeMap.entrySet()) {
            response = response.replaceAll(entry.getValue(), entry.getKey());
        }

        return response;
    }

    private String encodeUrl(String url, Map<String, String> encodeMap) {
        for (Map.Entry<String, String> entry : encodeMap.entrySet()) {
            url = url.replaceAll(entry.getKey(), entry.getValue());
        }

        return url;
    }

    @SneakyThrows
    public Map<String, String> urlEncodeWithUserInfo(List<String> urls) {

        Map<String, String> res = Maps.newHashMap();

        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            URI uri = new URI(url);

            String hostSrc = uri.getHost();
            String userInfo = uri.getUserInfo();

            String hostEncode = String.format("www.encode%s.com", i);
            String userInfoEncode = String.format("22f8b5b9-12b3-11da-0000-f41ab966581%s", i);

            res.put(hostSrc, hostEncode);
            res.put(userInfo, userInfoEncode);
        }

        return res;
    }

    public String cache(String url) {
        String key = UUID.randomUUID().toString();
        cache.put(key, url);

        return key;
    }
}
