### Java Offline Geoip

[![GitHub](https://img.shields.io/github/license/tornaia/java-offline-geoip.svg)](https://opensource.org/licenses/Apache-2.0)

##### Donate

Clear feedback. Just if you appreciate my efforts!

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=TAANNK2KXZXHG)

##### Intro

ISO 3166-1 alpha-2 codes are two-letter country codes defined in ISO 3166-1, part of the ISO 3166 standard published by the International Organization for Standardization (ISO), to represent countries, dependent territories, and special areas of geographical interest. [Read more](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)

##### How to use

```java
GeoIP geoIP = GeoIPProvider.getGeoIP();

Optional<String> optionalCountryIsoCode = geoIP.getTwoLetterCountryCode("50.63.202.32");
// optionalCountryIsoCode: Optional[US]

Optional<String> optionalCountryName = geoIP.getCountryName("50.63.202.32");
// optionalCountryName: Optional[United States]
```

##### Maven

```xml
<dependency>
  <groupId>com.github.tornaia</groupId>
  <artifactId>java-offline-geoip</artifactId>
  <version>0.1.10</version>
</dependency>
```

##### Prerequisites for development

* OpenJDK 8 or later (https://jdk.java.net/) 
* Maven 3.2.1 or later (https://maven.apache.org/download.cgi)

##### Build

* mvn clean package

##### License

The GeoLite2 databases are distributed under the Creative Commons Attribution-ShareAlike 4.0 International License. The attribution requirement may be met by including the following in all advertising and documentation mentioning features of or use of this database:

This product includes GeoLite2 data created by MaxMind, available from
<a href="http://www.maxmind.com">http://www.maxmind.com</a>.