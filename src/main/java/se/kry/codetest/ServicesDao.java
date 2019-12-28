package se.kry.codetest;

import io.vertx.core.json.JsonArray;

import java.util.HashMap;
import java.util.Map;

public class ServicesDao {

    private DBConnector connector;

    public ServicesDao(DBConnector connector) {
        this.connector = connector;
    }

    public Map<String, String> findAll() {
        Map<String, String> services = new HashMap<>();
        connector.query("select name from service;").setHandler(
                done -> {
                    if(done.succeeded()) {
                        done
                                .result()
                                .getResults()
                                .stream()
                                .forEach(
                                    jsonArray -> jsonArray.stream().forEach(
                                        name -> services.put((String) name, "UNKNOWN")
                                    )
                                );
                    } else {
                        done.cause().printStackTrace();
                    }
                }
        );
        return services;
    }

    public void addService(Service service) {
        String insertSql = "insert into service (name, url, creationdate) values (?, ?, ?);";
        JsonArray params = new JsonArray().add(service.getName()).add(service.getUrl()).add(service.getCreationDate());
        connector.query(insertSql, params).setHandler(
                done -> {
                    if(!done.succeeded()) {
                        done.cause().printStackTrace();
                    }
                }
        );
    }

    public void removeService(String name) {
        connector.query("delete from service where name = '"+ name +"';").setHandler(
                done -> {
                    if(!done.succeeded()) {
                        done.cause().printStackTrace();
                    }
                }
        );
    }
}
