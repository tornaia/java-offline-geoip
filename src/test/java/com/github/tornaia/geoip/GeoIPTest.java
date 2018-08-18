package com.github.tornaia.geoip;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GeoIPTest {

    @Test
    public void ipv4StringWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getNonResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("92.104.171.16");
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv6StringWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getNonResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("2001:4860:4860::8888");
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv4InetAddressWithNonResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getNonResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode(InetAddress.getByName("92.104.171.16"));
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv6InetAddressWithNonResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getNonResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv4StringWithResident() {
        GeoIP geoIP = GeoIPProvider.getResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("92.104.171.16");
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv6StringWithResident() throws UnknownHostException {
        GeoIP geoIP = GeoIPProvider.getResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv4InetAddressWithResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode(InetAddress.getByName("92.104.171.16"));
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv6InetAddressWithResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void ipBelongsToEUAndNotToAnyCountryThenReturnOptionalEmptyWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getNonResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("2.16.4.0");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void ipBelongsToEUAndNotToAnyCountryThenReturnOptionalEmptyWithResident() {
        GeoIP geoIP = GeoIPProvider.getResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("2.16.4.0");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void fallbackToGeonameIdWhichIsEUWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getNonResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("195.51.217.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void fallbackToGeonameIdWhichIsEUWithResident() {
        GeoIP geoIP = GeoIPProvider.getResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("195.51.217.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void noRegisteredCountryAndGeonameIdWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getNonResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("86.62.5.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void noRegisteredCountryAndGeonameIdWithResident() {
        GeoIP geoIP = GeoIPProvider.getResidentGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("86.62.5.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test(timeout = 10000)
    public void residentReallyInitializesJustOnce() {
        GeoIP geoIP = GeoIPProvider.getResidentGeoIP();
        for (int i = 0; i < 50; ++i) {
            Optional<String> optionalCountryIsoCode = geoIP.getCountryIsoCode("92.104.171.16");
            assertEquals("CH", optionalCountryIsoCode.get());
        }
    }
}
