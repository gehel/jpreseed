/**
 * Copyright (C) 2014 LedCom (guillaume.lederrey@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ledcom.jpreseed.web;

import ch.ledcom.jpreseed.UsbCreator;
import ch.ledcom.jpreseed.distro.DistroService;
import ch.ledcom.jpreseed.DownloaderFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class JPreseedWeb {

    private static final int MAX_CACHE_ENTRIES = 20;
    private static final int MAX_OBJECT_SIZE = 100 * 1024 * 1024;
    private static final int CONNECT_TIMEOUT = 1000;
    private static final int SOCKET_TIMEOUT = 1000;

    @Resource
    private ApplicationContext ctx;

    @Bean
    public DownloaderFactory downloaderFactory() {
        CacheConfig cacheConfig = CacheConfig.custom()
                .setMaxCacheEntries(MAX_CACHE_ENTRIES)
                .setMaxObjectSize(MAX_OBJECT_SIZE)
                .build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();

        return new DownloaderFactory(CachingHttpClients.custom()
                .setCacheConfig(cacheConfig)
                .setDefaultRequestConfig(requestConfig)
                .build());
    }

    @Bean
    public DistroService distroService() throws IOException, URISyntaxException {
        try (InputStream configuration = ctx.getResource("classpath:distributions.yaml").getInputStream()) {
            return DistroService.create(configuration);
        }
    }

    @Bean
    public UsbCreator usbCreator() {
        return new UsbCreator();
    }

    public static void main(String[] args) {
        SpringApplication.run(JPreseedWeb.class, args);
    }
}
