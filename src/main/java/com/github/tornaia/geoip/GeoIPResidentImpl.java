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
import static com.github.tornaia.geoip.GeoLiteConstants.LOCATIONS_CSV_COUNTRY_NAME;
import static com.github.tornaia.geoip.GeoLiteConstants.LOCATIONS_CSV_GEO_ID;

public class GeoIPResidentImpl implements GeoIP {

    private static class MapHolder {

        private static final Map<IpAddressMatcher, String> CIDR_NOTATIONS_TO_COUNTRY_ISO_CODE_MAP = getCidrNotationsToCountryIdoCodeMap();
        private static final Map<String, String> COUNTRY_ISO_CODE_TO_COUNTRY_NAME_MAP = getCountryIsoCodeToCountryNameMapInternal();

        private static Map<IpAddressMatcher, String> getCidrNotationsToCountryIdoCodeMap() {
            Map<IpAddressMatcher, String> cidrNotationsToCountryIsoCodeMap = new HashMap<>();
            Map<String, String> countryIdToCountryIsoCodeMap = getCountryIdToCountryIsoCodeMap();
            loadIpv4Csv(cidrNotationsToCountryIsoCodeMap, countryIdToCountryIsoCodeMap);
            loadIpv6Csv(cidrNotationsToCountryIsoCodeMap, countryIdToCountryIsoCodeMap);
            return cidrNotationsToCountryIsoCodeMap;
        }

        private static void loadIpv4Csv(Map<IpAddressMatcher, String> cidrNotationsToCountryIsoCodeMap, Map<String, String> countryIdToCountryIsoCodeMap) {
            loadIpvCsv(cidrNotationsToCountryIsoCodeMap, countryIdToCountryIsoCodeMap, "GeoLite2-Country-Blocks-IPv4.csv");
        }

        private static void loadIpv6Csv(Map<IpAddressMatcher, String> cidrNotationsToCountryIsoCodeMap, Map<String, String> countryIdToCountryIsoCodeMap) {
            loadIpvCsv(cidrNotationsToCountryIsoCodeMap, countryIdToCountryIsoCodeMap, "GeoLite2-Country-Blocks-IPv6.csv");
        }

        private static void loadIpvCsv(Map<IpAddressMatcher, String> cidrNotationsToCountryIsoCodeMap, Map<String, String> countryIdToCountryIsoCodeMap, String filename) {
            try (InputStream inputStream = MapHolder.class.getClassLoader().getResourceAsStream(filename)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                Map<IpAddressMatcher, String> ipMap = reader
                        .lines()
                        .skip(1)
                        .map(e -> e.split(","))
                        .filter(e -> !e[IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID].isEmpty() || !e[IPV_CSV_GEONAME_ID].isEmpty())
                        .collect(Collectors.toMap(e -> new IpAddressMatcher(e[LOCATIONS_CSV_GEO_ID]), e -> mapCountryCodeToCountryIsoCode(countryIdToCountryIsoCodeMap, e)));
                cidrNotationsToCountryIsoCodeMap.putAll(ipMap);
            } catch (IOException e) {
                throw new GeoIPException("Failed to read csv: " + filename, e);
            }
        }

        private static Map<String, String> getCountryIdToCountryIsoCodeMap() {
            try (InputStream inputStream = MapHolder.class.getClassLoader().getResourceAsStream("GeoLite2-Country-Locations-en.csv")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                return reader
                        .lines()
                        .skip(1)
                        .map(e -> e.split(","))
                        .collect(Collectors.toMap(e -> e[LOCATIONS_CSV_GEO_ID], e -> e[LOCATIONS_CSV_COUNTRY_CODE_ISO]));
            } catch (IOException e) {
                throw new GeoIPException("Failed to read GeoLite2-Country-Locations-en.csv", e);
            }
        }

        private static Map<String, String> getCountryIsoCodeToCountryNameMapInternal() {
            try (InputStream inputStream = MapHolder.class.getClassLoader().getResourceAsStream("GeoLite2-Country-Locations-en.csv")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                return reader
                        .lines()
                        .skip(1)
                        .map(e -> e.split(","))
                        .filter(e -> e[LOCATIONS_CSV_COUNTRY_CODE_ISO] != null && !e[LOCATIONS_CSV_COUNTRY_CODE_ISO].isEmpty())
                        .filter(e -> e[LOCATIONS_CSV_COUNTRY_NAME] != null && !e[LOCATIONS_CSV_COUNTRY_NAME].isEmpty())
                        .collect(Collectors.toMap(e -> e[LOCATIONS_CSV_COUNTRY_CODE_ISO], e -> stripLeadingAndTrailingDoubleQuotes(e[LOCATIONS_CSV_COUNTRY_NAME]), (a, b) -> a));
            } catch (IOException e) {
                throw new GeoIPException("Failed to read GeoLite2-Country-Locations-en.csv", e);
            }
        }

        private static String mapCountryCodeToCountryIsoCode(Map<String, String> countryIdToCountryIsoCodeMap, String[] splittedLine) {
            String registeredCountryGeonameId = splittedLine[IPV_CSV_REGISTERED_COUNTRY_GEONAME_ID];
            String geonameId = splittedLine[IPV_CSV_GEONAME_ID];
            String countryId = registeredCountryGeonameId.isEmpty() ? geonameId : registeredCountryGeonameId;
            return getCountryIsoCodeFromCountryId(countryIdToCountryIsoCodeMap, countryId);
        }

        private static String getCountryIsoCodeFromCountryId(Map<String, String> countryIdToCountryIsoCodeMap, String countryId) {
            String countryIsoCode = countryIdToCountryIsoCodeMap.get(countryId);
            boolean countryIsoCodeWasNotFoundForCountryId = countryIsoCode == null;
            if (countryIsoCodeWasNotFoundForCountryId) {
                throw new GeoIPException("Country iso code was not found for countryId, countryId: " + countryId);
            }
            return countryIsoCode;
        }

        private static String stripLeadingAndTrailingDoubleQuotes(String maybeQuotedCountryName) {
            String countryName = maybeQuotedCountryName;

            if (countryName.startsWith("\"")) {
                countryName = countryName.substring(1);
            }

            if (countryName.endsWith("\"")) {
                countryName = countryName.substring(0, countryName.length() - 1);
            }

            return countryName;
        }


        private static Map<IpAddressMatcher, String> getCidrNotationsToCountryIsoCodeMap() {
            return MapHolder.CIDR_NOTATIONS_TO_COUNTRY_ISO_CODE_MAP;
        }

        private static Map<String, String> getCountryIsoCodeToCountryNameMap() {
            return MapHolder.COUNTRY_ISO_CODE_TO_COUNTRY_NAME_MAP;
        }
    }

    @Override
    public Optional<String> getTwoLetterCountryCode(InetAddress inetAddress) {
        return getTwoLetterCountryCode(inetAddress.getHostAddress());
    }

    @Override
    public Optional<String> getTwoLetterCountryCode(String ipAddress) {
        return MapHolder.getCidrNotationsToCountryIsoCodeMap()
                .entrySet()
                .parallelStream()
                .filter(e -> e.getKey().matches(ipAddress))
                .map(Map.Entry::getValue)
                .filter(e -> !e.isEmpty())
                .findFirst();
    }

    @Override
    public Optional<String> getCountryName(InetAddress inetAddress) {
        return getCountryName(inetAddress.getHostAddress());
    }

    @Override
    public Optional<String> getCountryName(String ipAddress) {
        Optional<String> optionalTwoLetterCountryCode = getTwoLetterCountryCode(ipAddress);
        if (optionalTwoLetterCountryCode.isPresent()) {
            String twoLetterCountryCode = optionalTwoLetterCountryCode.get();
            Map<String, String> countryIsoCodeToCountryNameMap = MapHolder.getCountryIsoCodeToCountryNameMap();
            String countryName = countryIsoCodeToCountryNameMap.get(twoLetterCountryCode);
            return Optional.ofNullable(countryName);
        }
        return Optional.empty();
    }
}
