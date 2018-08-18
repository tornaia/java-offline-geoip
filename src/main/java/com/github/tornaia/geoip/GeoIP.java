package com.github.tornaia.geoip;

import java.net.InetAddress;
import java.util.Optional;

public interface GeoIP {

    Optional<String> getCountryIsoCode(InetAddress inetAddress);

    Optional<String> getCountryIsoCode(String ipAddress);
}
