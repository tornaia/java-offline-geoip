package com.github.tornaia.geoip;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

public class GeoLiteCsvTest {

    @Test
    public void firstLineOfIpv4CsvIsKnown() throws Exception {
        String ipv4Csv = readFully("GeoLite2-Country-Blocks-IPv4.csv");
        assertThat(ipv4Csv, startsWith("network,geoname_id,registered_country_geoname_id,represented_country_geoname_id,is_anonymous_proxy,is_satellite_provider"));
    }

    @Test
    public void firstLineOfIpv6CsvIsKnown() throws Exception {
        String ipv6Csv = readFully("GeoLite2-Country-Blocks-IPv6.csv");
        assertThat(ipv6Csv, startsWith("network,geoname_id,registered_country_geoname_id,represented_country_geoname_id,is_anonymous_proxy,is_satellite_provider"));
    }

    @Test
    public void firstLineOfLocationsCsvIsKnown() throws Exception {
        String locationsCsv = readFully("GeoLite2-Country-Locations-en.csv");
        assertThat(locationsCsv, startsWith("geoname_id,locale_code,continent_code,continent_name,country_iso_code,country_name,is_in_european_union"));
    }

    @Test
    public void contentLengthOfIpv4CsvIsOk() throws Exception {
        String ipv4Csv = readFully("GeoLite2-Country-Blocks-IPv4.csv");
        assertThat(ipv4Csv.length(), allOf(greaterThan(10 * 1024 * 1024), lessThan(20 * 1024 * 1024)));
    }

    @Test
    public void contentLengthOfIpv6CsvIsOk() throws Exception {
        String ipv6Csv = readFully("GeoLite2-Country-Blocks-IPv6.csv");
        assertThat(ipv6Csv.length(), allOf(greaterThan(2 * 1024 * 1024), lessThan(5 * 1024 * 1024)));
    }

    @Test
    public void contentLengthOfLocationsCsvIsOk() throws Exception {
        String locationsCsv = readFully("GeoLite2-Country-Locations-en.csv");
        assertThat(locationsCsv.length(), allOf(greaterThan(4 * 1024), lessThan(16 * 1024)));
    }

    private String readFully(String classpathFilename) throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(classpathFilename)) {
            StringBuilder sb = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                int c;
                while ((c = reader.read()) != -1) {
                    sb.append((char) c);
                }
            }
            return sb.toString();
        }
    }
}
