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
import static com.github.tornaia.geoip.GeoLiteConstants.LOCATIONS_CSV_COUNTRY_NAME;
import static com.github.tornaia.geoip.GeoLiteConstants.LOCATIONS_CSV_GEO_ID;

public class GeoIPNonResidentImpl implements GeoIP {

    @Override
    public Optional<String> getTwoLetterCountryCode(InetAddress inetAddress) {
        return getTwoLetterCountryCode(inetAddress.getHostAddress());
    }

    @Override
    public Optional<String> getTwoLetterCountryCode(String ipAddress) {
        Optional<String> optionalCountryCodeFromIpv4 = getCountryCodeFromIpv4(ipAddress);
        if (optionalCountryCodeFromIpv4.isPresent()) {
            return optionalCountryCodeFromIpv4;
        }

        return getCountryCodeFromIpv6(ipAddress);
    }

    @Override
    public Optional<String> getCountryName(InetAddress inetAddress) {
        return getCountryName(inetAddress.getHostAddress());
    }

    @Override
    public Optional<String> getCountryName(String ipAddress) {
        Optional<String> optionalCountryNameFromIpv4 = getCountryNameFromIpv4(ipAddress);
        if (optionalCountryNameFromIpv4.isPresent()) {
            return optionalCountryNameFromIpv4;
        }

        return getCountryNameFromIpv6(ipAddress);
    }

    private Optional<String> getCountryCodeFromIpv4(String ipAddress) {
        return findCountryCodeInCsv("GeoLite2-Country-Blocks-IPv4.csv", ipAddress);
    }

    private Optional<String> getCountryNameFromIpv4(String ipAddress) {
        return findCountryNameInCsv("GeoLite2-Country-Blocks-IPv4.csv", ipAddress);
    }

    private Optional<String> getCountryCodeFromIpv6(String ipAddress) {
        return findCountryCodeInCsv("GeoLite2-Country-Blocks-IPv6.csv", ipAddress);
    }

    private Optional<String> getCountryNameFromIpv6(String ipAddress) {
        return findCountryNameInCsv("GeoLite2-Country-Blocks-IPv6.csv", ipAddress);
    }

    private Optional<String> findCountryCodeInCsv(String filename, String ipAddress) {
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

    private Optional<String> findCountryNameInCsv(String filename, String ipAddress) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader
                    .lines()
                    .parallel()
                    .skip(1)
                    .map(e -> e.split(","))
                    .filter(e -> !e[IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID].isEmpty() || !e[IPV_CSV_GEONAME_ID].isEmpty())
                    .filter(e -> new IpAddressMatcher(e[LOCATIONS_CSV_GEO_ID]).matches(ipAddress))
                    .map(this::mapCountryCodeToCountryName)
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

    private String mapCountryCodeToCountryName(String[] splittedLine) {
        String registeredCountryGeonameId = splittedLine[IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID];
        String geonameId = splittedLine[IPV_CSV_GEONAME_ID];
        String countryId = registeredCountryGeonameId.isEmpty() ? geonameId : registeredCountryGeonameId;
        return getCountryNameFromCountryId(countryId);
    }

    private String getCountryIsoCodeFromCountryId(String countryId) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("GeoLite2-Country-Locations-en.csv")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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

    private String getCountryNameFromCountryId(String countryId) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("GeoLite2-Country-Locations-en.csv")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader
                    .lines()
                    .parallel()
                    .skip(1)
                    .map(e -> e.split(","))
                    .filter(e -> e[LOCATIONS_CSV_GEO_ID].equals(countryId))
                    .map(e -> e[LOCATIONS_CSV_COUNTRY_NAME])
                    .map(this::stripLeadingAndTrailingDoubleQuotes)
                    .findFirst()
                    .orElseThrow(() -> new GeoIPException("Country name was not found for countryId, countryId: " + countryId));
        } catch (IOException e) {
            throw new GeoIPException("Failed to read GeoLite2-Country-Locations-en.csv", e);
        }
    }

    private String stripLeadingAndTrailingDoubleQuotes(String maybeQuotedCountryName) {
        String countryName = maybeQuotedCountryName;

        if (countryName.startsWith("\"")) {
            countryName = countryName.substring(1);
        }

        if (countryName.endsWith("\"")) {
            countryName = countryName.substring(0, countryName.length() - 1);
        }

        return countryName;
    }
}
