package com.github.tornaia.geoip;

import com.github.tornaia.geoip.internal.IpAddressMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.tornaia.geoip.GeoLiteConstants.IPV_CSV_GEONAME_ID;
import static com.github.tornaia.geoip.GeoLiteConstants.IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID;
import static com.github.tornaia.geoip.GeoLiteConstants.LOCATIONS_CSV_COUNTRY_CODE_ISO;
import static com.github.tornaia.geoip.GeoLiteConstants.LOCATIONS_CSV_GEO_ID;

public class GeoIPResidentImpl implements GeoIP {

    private Map<IpAddressMatcher, String> cidrNotationsToCountryIsoCodeMap;

    @Override
    public Optional<String> getCountryIsoCode(InetAddress inetAddress) {
        return getCountryIsoCode(inetAddress.getHostAddress());
    }

    @Override
    public Optional<String> getCountryIsoCode(String ipAddress) {
        initIfNecessary();

        return cidrNotationsToCountryIsoCodeMap
                .entrySet()
                .parallelStream()
                .filter(e -> e.getKey().matches(ipAddress))
                .map(Map.Entry::getValue)
                .filter(e -> !e.isEmpty())
                .findFirst();
    }

    private void initIfNecessary() {
        if (cidrNotationsToCountryIsoCodeMap == null) {
            synchronized (this) {
                if (cidrNotationsToCountryIsoCodeMap == null) {
                    cidrNotationsToCountryIsoCodeMap = new HashMap<>();
                    Map<String, String> countryIdToCountryIsoCodeMap = getCountryIdToCountryIsoCodeMap();
                    loadIpv4Csv(countryIdToCountryIsoCodeMap);
                    loadIpv6Csv(countryIdToCountryIsoCodeMap);
                }
            }
        }
    }

    private void loadIpv4Csv(Map<String, String> countryIdToCountryIsoCodeMap) {
        loadIpvCsv(countryIdToCountryIsoCodeMap, "GeoLite2-Country-Blocks-IPv4.csv");
    }

    private void loadIpv6Csv(Map<String, String> countryIdToCountryIsoCodeMap) {
        loadIpvCsv(countryIdToCountryIsoCodeMap, "GeoLite2-Country-Blocks-IPv6.csv");
    }

    private void loadIpvCsv(Map<String, String> countryIdToCountryIsoCodeMap, String filename) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Map<IpAddressMatcher, String> ipv6Map = reader
                    .lines()
                    .parallel()
                    .skip(1)
                    .map(e -> e.split(","))
                    .filter(e -> !e[IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID].isEmpty() || !e[IPV_CSV_GEONAME_ID].isEmpty())
                    .collect(Collectors.toMap(e -> new IpAddressMatcher(e[LOCATIONS_CSV_GEO_ID]), e -> mapCountryCodeToCountryIsoCode(countryIdToCountryIsoCodeMap, e)));
            cidrNotationsToCountryIsoCodeMap.putAll(ipv6Map);
        } catch (IOException e) {
            throw new GeoIPException("Failed to read csv: " + filename, e);
        }
    }

    private Map<String, String> getCountryIdToCountryIsoCodeMap() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("GeoLite2-Country-Locations-en.csv")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader
                    .lines()
                    .parallel()
                    .skip(1)
                    .map(e -> e.split(","))
                    .collect(Collectors.toMap(e -> e[LOCATIONS_CSV_GEO_ID], e -> e[LOCATIONS_CSV_COUNTRY_CODE_ISO]));
        } catch (IOException e) {
            throw new GeoIPException("Failed to read GeoLite2-Country-Locations-en.csv", e);
        }
    }

    private String mapCountryCodeToCountryIsoCode(Map<String, String> countryIdToCountryIsoCodeMap, String[] splittedLine) {
        String registeredCountryGeonameId = splittedLine[IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID];
        String geonameId = splittedLine[IPV_CSV_GEONAME_ID];
        String countryId = registeredCountryGeonameId.isEmpty() ? geonameId : registeredCountryGeonameId;
        return getCountryIsoCodeFromCountryId(countryIdToCountryIsoCodeMap, countryId);
    }

    private String getCountryIsoCodeFromCountryId(Map<String, String> countryIdToCountryIsoCodeMap, String countryId) {
        String countryIsoCode = countryIdToCountryIsoCodeMap.get(countryId);
        boolean countryIsoCodeWasNotFoundForCountryId = countryIsoCode == null;
        if (countryIsoCodeWasNotFoundForCountryId) {
            throw new GeoIPException("Country iso code was not found for countryId, countryId: " + countryId);
        }
        return countryIsoCode;
    }
}
