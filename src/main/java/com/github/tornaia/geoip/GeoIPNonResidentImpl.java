package com.github.tornaia.geoip;

import com.github.tornaia.geoip.internal.IpAddressMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Optional;

import static com.github.tornaia.geoip.GeoLiteConstants.IPV_CSV_GEONAME_ID;
import static com.github.tornaia.geoip.GeoLiteConstants.IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID;
import static com.github.tornaia.geoip.GeoLiteConstants.LOCATIONS_CSV_COUNTRY_CODE_ISO;
import static com.github.tornaia.geoip.GeoLiteConstants.LOCATIONS_CSV_GEO_ID;

public class GeoIPNonResidentImpl implements GeoIP {

    @Override
    public Optional<String> getCountryIsoCode(InetAddress inetAddress) {
        return getCountryIsoCode(inetAddress.getHostAddress());
    }

    @Override
    public Optional<String> getCountryIsoCode(String ipAddress) {
        Optional<String> optionalCountryCodeFromIpv4 = getIpv4(ipAddress);
        if (optionalCountryCodeFromIpv4.isPresent()) {
            return optionalCountryCodeFromIpv4;
        }

        return getIpv6(ipAddress);
    }

    private Optional<String> getIpv4(String ipAddress) {
        return findInCsv("GeoLite2-Country-Blocks-IPv4.csv", ipAddress);
    }

    private Optional<String> getIpv6(String ipAddress) {
        return findInCsv("GeoLite2-Country-Blocks-IPv6.csv", ipAddress);
    }

    private Optional<String> findInCsv(String filename, String ipAddress) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader
                    .lines()
                    .parallel()
                    .skip(1)
                    .map(e -> e.split(","))
                    .filter(e -> !e[IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID].isEmpty() || !e[IPV_CSV_GEONAME_ID].isEmpty())
                    .filter(e -> new IpAddressMatcher(e[LOCATIONS_CSV_GEO_ID]).matches(ipAddress))
                    .map(this::mapCountryCodeToCountryIsoCode)
                    .filter(e -> !e.isEmpty())
                    .findFirst();
        } catch (IOException e) {
            throw new GeoIPException("Failed to read csv: " + filename, e);
        }
    }

    private String mapCountryCodeToCountryIsoCode(String[] splittedLine) {
        String registeredCountryGeonameId = splittedLine[IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID];
        String geonameId = splittedLine[IPV_CSV_GEONAME_ID];
        String countryId = registeredCountryGeonameId.isEmpty() ? geonameId : registeredCountryGeonameId;
        return getCountryIsoCodeFromCountryId(countryId);
    }

    private String getCountryIsoCodeFromCountryId(String countryId) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("GeoLite2-Country-Locations-en.csv")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader
                    .lines()
                    .parallel()
                    .skip(1)
                    .map(e -> e.split(","))
                    .filter(e -> e[LOCATIONS_CSV_GEO_ID].equals(countryId))
                    .map(e -> e[LOCATIONS_CSV_COUNTRY_CODE_ISO])
                    .findFirst()
                    .orElseThrow(() -> new GeoIPException("Country iso code was not found for countryId, countryId: " + countryId));
        } catch (IOException e) {
            throw new GeoIPException("Failed to read GeoLite2-Country-Locations-en.csv", e);
        }
    }
}
