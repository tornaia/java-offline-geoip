package com.github.tornaia.geoip;

public final class GeoIPProvider {

    public enum Type {
        RESIDENT, RESIDENT_LAZY, NON_RESIDENT
    }

    private GeoIPProvider() {
    }

    public static GeoIP getGeoIP() {
        return getGeoIP(Type.RESIDENT);
    }

    public static GeoIP getGeoIP(Type type) {
        switch (type) {
            case RESIDENT:
                return new GeoIPResidentImpl();
            case RESIDENT_LAZY:
                GeoIPResidentImpl residentGeoIP = new GeoIPResidentImpl();
                residentGeoIP.getTwoLetterCountryCode("127.0.0.1");
                return residentGeoIP;
            case NON_RESIDENT:
                return new GeoIPNonResidentImpl();
            default:
                throw new IllegalStateException("Unexpected type: " + type);
        }
    }
}
