package com.github.tornaia.geoip;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GeoIPTest {

    @Test
    public void ipv4StringWithDefault() {
        GeoIP geoIP = GeoIPProvider.getGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("92.104.171.16");
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv4StringWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("92.104.171.16");
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv6StringWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("2001:4860:4860::8888");
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv4InetAddressWithNonResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("92.104.171.16"));
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv6InetAddressWithNonResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv4StringWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("92.104.171.16");
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv6StringWithResident() throws UnknownHostException {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv4InetAddressWithResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("92.104.171.16"));
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void ipv6InetAddressWithResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void ipBelongsToEUAndNotToAnyCountryThenReturnOptionalEmptyWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("2.16.4.0");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void ipBelongsToEUAndNotToAnyCountryThenReturnOptionalEmptyWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("2.16.4.0");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void fallbackToGeonameIdWhichIsEUWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("195.51.217.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void fallbackToGeonameIdWhichIsEUWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("195.51.217.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void noRegisteredCountryAndGeonameIdWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("86.62.5.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void noRegisteredCountryAndGeonameIdWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("86.62.5.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void residentReallyInitializesJustOnce() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 50; ++i) {
            Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("92.104.171.16");
            assertEquals("CH", optionalCountryIsoCode.get());
        }
        long duration = System.currentTimeMillis() - start;

        assertThat(duration, lessThan(5000L));
    }
}
