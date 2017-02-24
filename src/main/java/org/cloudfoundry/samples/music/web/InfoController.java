package org.cloudfoundry.samples.music.web;

import org.cloudfoundry.samples.music.domain.ApplicationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.jdbc.pool.DataSource;

import sun.net.InetAddressCachePolicy;
import sun.net.spi.nameservice.dns.DNSNameService;

@RestController
public class InfoController {
    @Autowired(required = false)
    private Cloud cloud;

    private Environment springEnvironment;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public InfoController(Environment springEnvironment) {
        this.springEnvironment = springEnvironment;
    }

    @RequestMapping(value = "/appinfo")
    public ApplicationInfo info() {
        String jdbcUrl = springEnvironment.getProperty("spring.datasource.url");
        if (jdbcUrl == null) {
            jdbcUrl = dataSource.getUrl();
        }
        String jdbcHost = "unknown";
        String jdbcIp = "?";
        if (StringUtils.hasText(jdbcUrl)) {
            jdbcHost = jdbcUrl.substring(jdbcUrl.indexOf("//") + 2);
            if (jdbcHost.contains(":")) {
                jdbcHost = jdbcHost.substring(0, jdbcHost.indexOf(":"));
            }
            else {
                if (jdbcHost.contains("/")) {
                    jdbcHost = jdbcHost.substring(0, jdbcHost.indexOf("/"));
                } else {
                    if (jdbcHost.contains("?")) {
                        jdbcHost = jdbcHost.substring(0, jdbcHost.indexOf("?"));
                    }
                }
            }
            try {
                DNSNameService ns = new DNSNameService();
                InetAddress address = InetAddress.getByName(jdbcHost);
                jdbcIp = address.getHostAddress();
            } catch (UnknownHostException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String sunCacheTtl = System.getProperty("sun.net.inetaddr.ttl");
        String sunCacheNegTtl = System.getProperty("sun.net.inetaddr.negative.ttl");
        String dnsCacheTtl = java.security.Security.getProperty("networkaddress.cache.ttl");
        String dnsCacheNegTtl = java.security.Security.getProperty("networkaddress.cache.negative.ttl");
        String cachePolicy = "sun.net.InetAddressCachePolicy#cachePolicy: " + InetAddressCachePolicy.get();
        String cacheNegPolicy = "sun.net.InetAddressCachePolicy#negativeCachePolicy: " + InetAddressCachePolicy.getNegative();
        return new ApplicationInfo(springEnvironment.getActiveProfiles(), getServiceNames(),
                new String[] {
                        "Config:javax.sql.Datasource#class: " + dataSource.getClass().getName(),
                        "Config:javax.sql.Datasource#url: " + dataSource.getUrl(),
                        "Config:spring.datasource.url: " + jdbcUrl,
                        "Config:mysql.host: " + jdbcHost + " : " + jdbcIp,
                        "System:sun.net.inetaddr.ttl: " + sunCacheTtl,
                        "System:sun.net.inetaddr.negative.ttl: " + sunCacheNegTtl,
                        "Security:networkaddress.cache.ttl: " + dnsCacheTtl,
                        "Security:networkaddress.cache.negative.ttl: " + dnsCacheNegTtl,
                        cachePolicy,
                        cacheNegPolicy});
    }

    @RequestMapping(value = "/service")
    public List<ServiceInfo> showServiceInfo() {
        if (cloud != null) {
            return cloud.getServiceInfos();
        } else {
            return new ArrayList<>();
        }
    }

    private String[] getServiceNames() {
        if (cloud != null) {
            final List<ServiceInfo> serviceInfos = cloud.getServiceInfos();

            List<String> names = new ArrayList<>();
            for (ServiceInfo serviceInfo : serviceInfos) {
                names.add(serviceInfo.getId());
            }
            return names.toArray(new String[names.size()]);
        } else {
            return new String[]{};
        }
    }
}