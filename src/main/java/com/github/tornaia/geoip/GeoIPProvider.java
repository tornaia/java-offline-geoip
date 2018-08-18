package com.github.tornaia.geoip;

public class GeoIPProvider {

    public static GeoIP getNonResidentGeoIP() {
        return new GeoIPNonResidentImpl();
    }

    public static GeoIP getResidentGeoIP() {
        return new GeoIPResidentImpl();
    }
}
