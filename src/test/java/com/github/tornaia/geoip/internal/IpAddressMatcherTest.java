package com.github.tornaia.geoip.internal;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IpAddressMatcherTest {

    @Test
    public void canParseCirdNotation() {
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher("2.16.4.0/23");
        assertNotNull(ipAddressMatcher);
    }
}
