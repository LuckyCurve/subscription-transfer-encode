package space.luckycurve.subscribe;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SubscribeController {

    private SubscribeService subScribeService;

    @GetMapping(value = "/sub", produces = "text/plain;charset=utf-8")
    public String sub(@RequestParam(value = "url", required = false) String url,
                      @RequestParam(value = "key", required = false) String key) {
        return subScribeService.sub(url, key);
    }


    @GetMapping("/cache")
    public String cache(@RequestParam("url") String url) {
        return subScribeService.cache(url);
    }
}
