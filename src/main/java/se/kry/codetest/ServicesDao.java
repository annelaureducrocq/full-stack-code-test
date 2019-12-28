package se.kry.codetest;

import java.util.HashMap;
import java.util.Map;

public class ServicesDao {

    private DBConnector connector;

    public ServicesDao(DBConnector connector) {
        this.connector = connector;
    }

    public Map<String, String> findAll() {
        Map<String, String> services = new HashMap<>();
        connector.query("select url from service;").setHandler(
                done -> {
                    if(done.succeeded()) {
                        done
                                .result()
                                .getResults()
                                .stream()
                                .forEach(
                                    jsonArray -> jsonArray.stream().forEach(
                                        url -> services.put((String) url, "UNKNOWN")
                                    )
                                );
                    } else {
                        done.cause().printStackTrace();
                    }
                }
        );
        return services;
    }

    public void addService(String url) {
        connector.query("insert into service (url) values ('"+ url +"');").setHandler(
                done -> {
                    if(!done.succeeded()) {
                        done.cause().printStackTrace();
                    }
                }
        );
    }

    public void removeService(String url) {
        connector.query("delete from service where url = '"+ url +"';").setHandler(
                done -> {
                    if(!done.succeeded()) {
                        done.cause().printStackTrace();
                    }
                }
        );
    }
}
