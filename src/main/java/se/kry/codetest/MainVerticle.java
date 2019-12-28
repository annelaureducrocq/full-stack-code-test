package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  private Map<String, String> services;
  //TODO use this
  private DBConnector connector;
  private ServicesDao servicesDao;
  private BackgroundPoller poller = new BackgroundPoller();

  @Override
  public void start(Future<Void> startFuture) {
    connector = new DBConnector(vertx);
    servicesDao = new ServicesDao(connector);

    this.services = servicesDao.findAll();

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices(services));
    setRoutes(router);
    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, result -> {
          if (result.succeeded()) {
            System.out.println("KRY code test service started");
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private void setRoutes(Router router){
    router.route("/*").handler(StaticHandler.create());
    router.get("/service").handler(req -> {
      List<JsonObject> jsonServices = services
          .entrySet()
          .stream()
          .map(service ->
              new JsonObject()
                  .put("name", service.getKey())
                  .put("status", service.getValue()))
          .collect(Collectors.toList());
      req.response()
          .putHeader("content-type", "application/json")
          .end(new JsonArray(jsonServices).encode());
    });
    router.post("/service").handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();
      Service service = new Service(jsonBody.getString("name"), jsonBody.getString("url"), Instant.now());
      if(jsonBody.getString("name") == null || jsonBody.getString("url") == null){
        req.response().setStatusCode(400).end("You should fill name & url attributes !");
      } else if(services.containsKey(service.getName())) {
        req.response().setStatusCode(400).end("Service name " + service.getName() + " already exists");
      } else {
        services.put(service.getName(), "UNKNOWN");
        servicesDao.addService(service);
        req.response()
                .putHeader("content-type", "text/plain")
                .end("OK");
      }
    });
    router.delete("/service/:name").handler(routingContext -> {
      String name = routingContext.pathParam("name");
      HttpServerResponse response = routingContext.response();
      if (services.containsKey(name)) {
        services.remove(name);
        servicesDao.removeService(name);
        response.setStatusCode(204).end();
      }
      response.setStatusCode(404).end();
    });
  }

}



