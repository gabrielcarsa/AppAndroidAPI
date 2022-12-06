package com.example.provajava;

public class Country {
    private String name;
    private String region;
    private String flag;

    public Country(String name, String region, String flag) {
        this.name = name;
        this.region = region;
        this.flag = flag;
    }

    public Country() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
