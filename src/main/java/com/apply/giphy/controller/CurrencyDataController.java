package com.apply.giphy.controller;

import com.apply.giphy.model.CurrencyData;
import com.apply.giphy.util.CurrencyDataUtil;
import com.apply.giphy.util.GiphyUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/currency")
public class CurrencyDataController {

    @Value("${openexchange.APP_ID}")
    private String APP_ID;

    @Value("${openexchange.BASE}")
    private String BASE;

    @Value("${giphy.API_KEY}")
    private String API_KEY;

    @Autowired
    private CurrencyDataUtil currencyDataUtil;

    @Autowired
    private GiphyUtil giphyUtil;

    @RequestMapping("/{currency}")
    public String getCurrencyChange(@PathVariable String currency) {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.minusDays(1);
        String previousJson =  currencyDataUtil.getPrevious(localDate.toString(), APP_ID, BASE, currency);
        String latestJson = currencyDataUtil.getLatest(APP_ID, BASE, currency);

        Gson gson = new Gson();
        Double previousPrice = gson.fromJson(previousJson, CurrencyData.class).getRates().get(currency);
        Double latestPrice = gson.fromJson(latestJson, CurrencyData.class).getRates().get(currency);
        String giphyResponse = latestPrice > previousPrice ? giphyUtil.getGifRich(API_KEY) : giphyUtil.getGifBroke(API_KEY);
        JsonObject giphyJson = JsonParser.parseString(giphyResponse).getAsJsonObject();
        String gifLink = giphyJson
                .getAsJsonArray("data")
                .get(ThreadLocalRandom.current().nextInt(0, 26))
                .getAsJsonObject()
                .getAsJsonObject("images")
                .getAsJsonObject("original")
                .get("url").toString();

        return "<img src=" + gifLink +
                " style=\"position: fixed;\n" +
                "    top: 50%;\n" +
                "    left: 50%;\n" +
                "    transform: translate(-50%, -50%);\"></img>";
    }


}
