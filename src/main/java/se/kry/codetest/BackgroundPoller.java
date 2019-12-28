package se.kry.codetest;

import io.vertx.core.Future;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BackgroundPoller {

  public Future<List<String>> pollServices(Map<String, String> services) {
    Future<List<String>> futures = Future.future();
    List<String> results = services
            .entrySet()
            .stream()
            .map(service -> getStatus(service.getKey()))
            .collect(Collectors.toList());
    futures.complete(results);
    return futures;
  }

  private String getStatus(String url) {
    int code = 0;
    try {
      URL siteURL = new URL(url);
      HttpURLConnection connection = null;
      connection = (HttpURLConnection) siteURL.openConnection();

      connection.setRequestMethod("GET");
      connection.setConnectTimeout(3000);
      connection.connect();
      code = connection.getResponseCode();

    } catch (IOException e) {
      e.printStackTrace();
      return "FAIL";
    }
    if (code == 200) {
      return "OK";
    }
    return "FAIL";
  }
}
