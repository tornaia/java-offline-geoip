package com.github.tornaia.geoip;

public final class GeoIPProvider {

    private GeoIPProvider() {
    }

    public static GeoIP getNonResidentGeoIP() {
        return new GeoIPNonResidentImpl();
    }

    public static GeoIP getResidentGeoIP() {
        return new GeoIPResidentImpl();
    }
}
