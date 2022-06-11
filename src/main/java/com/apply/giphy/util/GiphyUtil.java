package com.apply.giphy.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="giphy", url="${giphy.URL}")
public interface GiphyUtil {
    @GetMapping("/search?api_key={API_KEY}&q=rich")
    String getGifRich(@PathVariable String API_KEY);

    @GetMapping("/search?api_key={API_KEY}&q=broke")
    String getGifBroke(@PathVariable String API_KEY);
}
