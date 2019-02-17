package org.revo.domain;

public class Token {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null) value = "";
        String[] split = value.split(";");
        this.value = split.length > 0 ? split[0].replace("XSRF-TOKEN=", "") : "";
    }
}