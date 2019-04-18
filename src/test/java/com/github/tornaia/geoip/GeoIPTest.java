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
    public void getTwoLetterCountryCodeIPv4StringWithDefault() {
        GeoIP geoIP = GeoIPProvider.getGeoIP();
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("92.104.171.16");
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPv4StringWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("92.104.171.16");
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPv6StringWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("2001:4860:4860::8888");
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPv4InetAddressWithNonResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("92.104.171.16"));
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPv6InetAddressWithNonResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPv4StringWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("92.104.171.16");
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPv6StringWithResident() throws UnknownHostException {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPv4InetAddressWithResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("92.104.171.16"));
        assertEquals("CH", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPv6InetAddressWithResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("US", optionalCountryIsoCode.get());
    }

    @Test
    public void getTwoLetterCountryCodeIPBelongsToEUAndNotToAnyCountryThenReturnOptionalEmptyWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("2.16.4.0");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void getTwoLetterCountryCodeIPBelongsToEUAndNotToAnyCountryThenReturnOptionalEmptyWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("2.16.4.0");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void getTwoLetterCountryCodeIPFallbackToGeonameIdWhichIsEUWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("195.51.217.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void getTwoLetterCountryCodeIPFallbackToGeonameIdWhichIsEUWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("195.51.217.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void getTwoLetterCountryCodeIPNoRegisteredCountryAndGeonameIdWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("86.62.5.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void getTwoLetterCountryCodeIPNoRegisteredCountryAndGeonameIdWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("86.62.5.1");
        assertFalse(optionalCountryIsoCode.isPresent());
    }

    @Test
    public void getTwoLetterCountryCodeIPResidentReallyInitializesJustOnce() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 50; ++i) {
            Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("92.104.171.16");
            assertEquals("CH", optionalCountryIsoCode.get());
        }
        long duration = System.currentTimeMillis() - start;

        assertThat(duration, lessThan(5000L));
    }

    @Test
    public void getCountryNameIPv4StringWithDefault() {
        GeoIP geoIP = GeoIPProvider.getGeoIP();
        Optional<String> optionalCountryName = geoIP.getCountryName("92.104.171.16");
        assertEquals("Switzerland", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPv4StringWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("92.104.171.16");
        assertEquals("Switzerland", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPv6StringWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("2001:4860:4860::8888");
        assertEquals("United States", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPv4InetAddressWithNonResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName(InetAddress.getByName("92.104.171.16"));
        assertEquals("Switzerland", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPv6InetAddressWithNonResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("United States", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPv4StringWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("92.104.171.16");
        assertEquals("Switzerland", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPv6StringWithResident() throws UnknownHostException {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("United States", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPv4InetAddressWithResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName(InetAddress.getByName("92.104.171.16"));
        assertEquals("Switzerland", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPv6InetAddressWithResident() throws Exception {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName(InetAddress.getByName("2001:4860:4860::8888"));
        assertEquals("United States", optionalCountryName.get());
    }

    @Test
    public void getCountryNameIPBelongsToEUAndNotToAnyCountryThenReturnOptionalEmptyWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("2.16.4.0");
        assertFalse(optionalCountryName.isPresent());
    }

    @Test
    public void getCountryNameIPBelongsToEUAndNotToAnyCountryThenReturnOptionalEmptyWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("2.16.4.0");
        assertFalse(optionalCountryName.isPresent());
    }

    @Test
    public void getCountryNameIPFallbackToGeonameIdWhichIsEUWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("195.51.217.1");
        assertFalse(optionalCountryName.isPresent());
    }

    @Test
    public void getCountryNameIPFallbackToGeonameIdWhichIsEUWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("195.51.217.1");
        assertFalse(optionalCountryName.isPresent());
    }

    @Test
    public void getCountryNameIPNoRegisteredCountryAndGeonameIdWithNonResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("86.62.5.1");
        assertFalse(optionalCountryName.isPresent());
    }

    @Test
    public void getCountryNameIPNoRegisteredCountryAndGeonameIdWithResident() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        Optional<String> optionalCountryName = geoIP.getCountryName("86.62.5.1");
        assertFalse(optionalCountryName.isPresent());
    }

    @Test
    public void getCountryNameIPResidentReallyInitializesJustOnce() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 50; ++i) {
            Optional<String> optionalCountryName = geoIP.getCountryName("92.104.171.16");
            assertEquals("Switzerland", optionalCountryName.get());
        }
        long duration = System.currentTimeMillis() - start;

        assertThat(duration, lessThan(5000L));
    }
}
