package com.github.tornaia.geoip;

public class GeoIPException extends RuntimeException {

    private static final long serialVersionUID = -2363078259040227858L;

    public GeoIPException(String message) {
        super(message);
    }

    public GeoIPException(String message, Exception cause) {
        super(message, cause);
    }

}
