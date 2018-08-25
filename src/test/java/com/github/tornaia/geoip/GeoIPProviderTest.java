package com.github.tornaia.geoip;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class GeoIPProviderTest {

    @Test
    public void byDefaultResidentImplIsCreated() {
        GeoIP geoIP = GeoIPProvider.getGeoIP();
        assertThat(geoIP, instanceOf(GeoIPResidentImpl.class));
    }

    @Test
    public void residentTypeCreatesResidentImpl() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT);
        assertThat(geoIP, instanceOf(GeoIPResidentImpl.class));
    }

    @Test
    public void residentLazyTypeCreatesResidentImpl() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.RESIDENT_LAZY);
        assertThat(geoIP, instanceOf(GeoIPResidentImpl.class));
    }

    @Test
    public void nonResidentTypeCreatesNonResidentImpl() {
        GeoIP geoIP = GeoIPProvider.getGeoIP(GeoIPProvider.Type.NON_RESIDENT);
        assertThat(geoIP, instanceOf(GeoIPNonResidentImpl.class));
    }
}
