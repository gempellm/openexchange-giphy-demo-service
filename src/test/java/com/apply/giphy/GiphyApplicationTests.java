package com.apply.giphy;

import com.apply.giphy.model.CurrencyData;
import com.apply.giphy.util.CurrencyDataUtil;
import com.apply.giphy.util.GiphyUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GiphyApplicationTests {
	
	private static WireMockServer wireMockServer;

	@Autowired
	private GiphyUtil giphyUtil;

	@Autowired
	private CurrencyDataUtil currencyDataUtil;

	@Value("${openexchange.APP_ID}")
	private String APP_ID;

	@Value("${openexchange.BASE}")
	private String BASE;

	@Value("${giphy.API_KEY}")
	private String API_KEY;

	@BeforeAll
	static void init() {
		wireMockServer = new WireMockServer(
				new WireMockConfiguration().port(7070)
		);
		wireMockServer.start();
		WireMock.configureFor("localhost", 7070);
	}

	@Test
	void testCallCurrencyDataUtilGetLatestPriceFromJson() throws  Exception {
		String currency = "RUB";
		stubFor(WireMock.get(urlMatching("/latest.json\\?app_id="+APP_ID+"&base="+BASE+"&symbols="+currency))
		.willReturn(aResponse().withBody("{\n" +
				"  \"disclaimer\": \"Usage subject to terms: https://openexchangerates.org/terms\",\n" +
				"  \"license\": \"https://openexchangerates.org/license\",\n" +
				"  \"timestamp\": 1654945208,\n" +
				"  \"base\": \"USD\",\n" +
				"  \"rates\": {\n" +
				"    \"RUB\": 57.624893\n" +
				"  }\n" +
				"}")));

		String latestJson = currencyDataUtil.getLatest(APP_ID, BASE, currency);
		Gson gson = new Gson();
		Double latestPrice = gson.fromJson(latestJson, CurrencyData.class).getRates().get(currency);

		Assert.notNull(latestPrice, "latestPrice is NULL!");
		Assert.isTrue(latestPrice > 0, "latestPrice is less than 0!");
	}

	@Test
	void testCallCurrencyDataUtilGetPreviousPriceFromJson() throws  Exception {
		String currency = "RUB";
		LocalDate localDate = LocalDate.now();
		localDate = localDate.minusDays(1);
		stubFor(WireMock.get(urlMatching("/historical/"+localDate.toString()+".json\\?app_id="+APP_ID+"&base="+BASE+"&symbols="+currency))
				.willReturn(aResponse().withBody("{\n" +
						"  \"disclaimer\": \"Usage subject to terms: https://openexchangerates.org/terms\",\n" +
						"  \"license\": \"https://openexchangerates.org/license\",\n" +
						"  \"timestamp\": 1654819159,\n" +
						"  \"base\": \"USD\",\n" +
						"  \"rates\": {\n" +
						"    \"RUB\": 58.000001\n" +
						"  }\n" +
						"}")));

		String previousJson = currencyDataUtil.getPrevious(localDate.toString(), APP_ID, BASE, currency);
		Gson gson = new Gson();
		Double previousPrice = gson.fromJson(previousJson, CurrencyData.class).getRates().get(currency);

		Assert.notNull(previousPrice, "previousPrice is NULL!");
		Assert.isTrue(previousPrice > 0, "previousPrice is less than 0!");
	}

	@Test
	void testCallGiphyUtilGetGifRichUrlFromJson() throws Exception {
		File resource = new ClassPathResource("giphyRichResponse.json").getFile();
		String giphyMockJson = new String(Files.readAllBytes(resource.toPath()));

		stubFor(WireMock.get(urlMatching("/search\\?api_key="+API_KEY+"&q=rich"))
				.willReturn(aResponse().withBody(giphyMockJson)));

		String giphyResponse = giphyUtil.getGifRich(API_KEY);
		JsonObject giphyJson = JsonParser.parseString(giphyResponse).getAsJsonObject();
		String gifLink = giphyJson
				.getAsJsonArray("data")
				.get(0)
				.getAsJsonObject()
				.getAsJsonObject("images")
				.getAsJsonObject("original")
				.get("url").toString();

		Assert.isTrue(gifLink.equals("\"https://media1.giphy.com/media/LdOyjZ7io5Msw/giphy.gif?cid=6cdfd481630hmkcbwjvrsmt2618ahzxx3tlo7ir03oso7xso&rid=giphy.gif&ct=g\""), "Rich Gif link "+gifLink+" is not equals to the expected!");
	}

	@Test
	void testCallGiphyUtilGetGifBrokeUrlFromJson() throws Exception {
		File resource = new ClassPathResource("giphyBrokeResponse.json").getFile();
		String giphyMockJson = new String(Files.readAllBytes(resource.toPath()));

		stubFor(WireMock.get(urlMatching("/search\\?api_key="+API_KEY+"&q=broke"))
				.willReturn(aResponse().withBody(giphyMockJson)));

		String giphyResponse = giphyUtil.getGifBroke(API_KEY);
		JsonObject giphyJson = JsonParser.parseString(giphyResponse).getAsJsonObject();
		String gifLink = giphyJson
				.getAsJsonArray("data")
				.get(0)
				.getAsJsonObject()
				.getAsJsonObject("images")
				.getAsJsonObject("original")
				.get("url").toString();

		Assert.isTrue(gifLink.equals("\"https://media2.giphy.com/media/ZGH8VtTZMmnwzsYYMf/giphy.gif?cid=6cdfd48174hc941ukzg91kibsqi8c74uxn011zygf0uhvn8f&rid=giphy.gif&ct=g\""), "Broke Gif link "+gifLink+" is not equals to the expected!");
	}

}
