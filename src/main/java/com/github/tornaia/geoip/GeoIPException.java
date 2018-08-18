package com.github.tornaia.geoip;

public class GeoIPException extends RuntimeException {

    public GeoIPException(String message) {
        super(message);
    }

    public GeoIPException(String message, Exception cause) {
        super(message, cause);
    }

}
