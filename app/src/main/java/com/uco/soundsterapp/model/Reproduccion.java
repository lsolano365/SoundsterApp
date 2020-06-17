package com.uco.soundsterapp.model;

public class Reproduccion {

    public String id;
    public String name;
    public String url;

    public Reproduccion() {

    }

    public Reproduccion(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
