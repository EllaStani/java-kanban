package client;

import utility.ManagerSaveException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVTaskClient {
    private String url;
    private String apiToken;

    public KVTaskClient(int port) {
        this.url = "http://localhost:" + port + "/";
        this.apiToken = register(url);
    }

    private String register(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register/"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("can't register a request, statusCode = " + response.statusCode());
            }
            return response.body();
        } catch (Exception exception) {
            throw new ManagerSaveException(exception.toString());
        }
    }

    public String load(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + this.apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("can't register a request, statusCode = " + response.statusCode());
            }
            return response.body();
        } catch (Exception e) {
            throw new ManagerSaveException(e.toString());
        }
    }

    public String put(String key, String json) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json, UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + this.apiToken))
                    .POST(body)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("can't register a request, statusCode = " + response.statusCode());
            }
            return response.body();
        } catch (Exception exception) {
            throw new ManagerSaveException(exception.toString());
        }
    }
}
