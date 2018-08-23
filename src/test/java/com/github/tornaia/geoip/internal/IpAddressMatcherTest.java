package com.github.tornaia.geoip.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IpAddressMatcherTest {

    @Test
    public void canParseCirdNotation() {
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher("2.16.4.0/23");
        assertNotNull(ipAddressMatcher);
    }
}
