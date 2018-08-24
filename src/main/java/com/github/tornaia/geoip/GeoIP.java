package com.github.tornaia.geoip;

import java.net.InetAddress;
import java.util.Optional;

public interface GeoIP {

    /**
     * Returns the country code for a specific inetAddress. For example, this would be CH for the
     * 85.1.242.106, and HU for 81.182.2.4.
     *
     * @param inetAddress the inetAddress that we want to get the country code for
     * @return two letter country code for the inetAddress denoted by inetAddress
     * @see <a href="https://en.wikipedia.org/wiki/ISO_3166-2">Wikipedia: ISO_3166-2</a>
     */
    Optional<String> getTwoLetterCountryCode(InetAddress inetAddress);

    /**
     * Returns the country code for a specific ip address. For example, this would be CH for the
     * 85.1.242.106, and HU for 81.182.2.4.
     *
     * @param ipAddress the ip address that we want to get the country code for
     * @return two letter country code for the ip address denoted by inetAddress
     * @see <a href="https://en.wikipedia.org/wiki/ISO_3166-2">Wikipedia: ISO_3166-2</a>
     */
    Optional<String> getTwoLetterCountryCode(String ipAddress);
}
