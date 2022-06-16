package lab3;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CasinoConnector {
    private static final String casinoURL = "http://95.217.177.249/casino";

    private static JsonObject sendRequest(String url) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("URL = " + url);
        System.out.println("response code = " + response.statusCode());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            JsonParser parser = new JsonParser();
            return parser.parse(response.body()).getAsJsonObject();
        } else {
            return null;
        }
    }

    public static boolean createAccount(Account acc) throws IOException, InterruptedException {
        String url = casinoURL + "/createacc?id=" + acc.id;
        JsonObject jsonObject = sendRequest(url);
        if (jsonObject == null) {
            return false;
        }

        // parse json
        acc.money = jsonObject.get("money").getAsJsonPrimitive().getAsLong();
        return true;
    }

    public static boolean makeBetAndPlay(Account acc, long betMoney, long number) throws IOException, InterruptedException {
        String url = casinoURL + "/play" + acc.mode + "?id=" + acc.id + "&bet=" + betMoney + "&number=" + number;
        JsonObject jsonObject = sendRequest(url);
        if (jsonObject == null) {
            return false;
        }
        // parse json
        acc.lastMessage = jsonObject.get("message").getAsJsonPrimitive().getAsString();
        acc.lastRealNumber = jsonObject.get("realNumber").getAsJsonPrimitive().getAsLong();
        // update money
        jsonObject = jsonObject.get("account").getAsJsonObject();
        acc.money = jsonObject.get("money").getAsJsonPrimitive().getAsLong();
        return true;
    }
}
