package se.kry.codetest;

import java.time.Instant;

public class Service {

    private String name;

    private String url;

    private Instant creationDate;

    public Service(String name, String url, Instant creationDate) {
        this.name = name;
        this.url = url;
        this.creationDate = creationDate;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }
}
