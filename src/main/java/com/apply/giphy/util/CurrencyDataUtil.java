package com.apply.giphy.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="openexchange", url="${openexchange.URL}")
public interface CurrencyDataUtil {

    @GetMapping("/latest.json?app_id={appId}&base={base}&symbols={currency}")
    String getLatest(@PathVariable String appId,
                     @PathVariable String base,
                     @PathVariable String currency);

    @GetMapping("/historical/{date}.json?app_id={appId}&base={base}&symbols={currency}")
    String getPrevious(@PathVariable String date,
                       @PathVariable String appId,
                       @PathVariable String base,
                       @PathVariable String currency);
}
