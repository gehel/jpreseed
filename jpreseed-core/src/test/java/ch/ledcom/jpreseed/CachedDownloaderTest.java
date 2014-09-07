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
package ch.ledcom.jpreseed;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.net.HttpHeaders.ETAG;
import static com.google.common.net.HttpHeaders.IF_NONE_MATCH;
import static com.google.common.net.HttpHeaders.LAST_MODIFIED;
import static org.assertj.core.api.Assertions.assertThat;

public class CachedDownloaderTest {
    private static final int HTTP_PORT = 9999;
    private static final String TEST_FILENAME = "/test.txt";
    private static final String URL_UNDER_TEST = "http://localhost:" + HTTP_PORT + TEST_FILENAME;
    private static final String FILE_CONTENT = "Hello world !";

    @Rule
    @VisibleForTesting
    public WireMockRule wireMockRule = new WireMockRule(HTTP_PORT);

    private DownloaderFactory downloaderFactory;

    @Before
    public final void createDownloader() {
            CacheConfig cacheConfig = CacheConfig.custom()
                    .setMaxCacheEntries(20)
                    .setMaxObjectSize(100 * 1024 * 1024)
                    .build();
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(1000)
                    .setSocketTimeout(1000)
                    .build();

        downloaderFactory = new DownloaderFactory(CachingHttpClients.custom()
                .setCacheConfig(cacheConfig)
                .setDefaultRequestConfig(requestConfig)
                .build());
    }

    @Test
    public final void downloadCreateFileOnDisk() throws URISyntaxException, IOException {
        stubFileToDownload();
        try (
                Downloader downloader = downloaderFactory.getDownloader(new URI(URL_UNDER_TEST));
                InputStream content = downloader.getContent()) {
            assertThat(content).hasContentEqualTo(expextedContent());
            verify(getRequestedFor(urlEqualTo(TEST_FILENAME)));
        }
    }

    private InputStream expextedContent() {
        return new ByteArrayInputStream(FILE_CONTENT.getBytes(UTF_8));
    }

    @Test
    public final void secondDownloadGetsFileFromCache() throws URISyntaxException, IOException {
        stubFileToDownload();

        try (
                Downloader downloader = downloaderFactory.getDownloader(new URI(URL_UNDER_TEST));
                InputStream content = downloader.getContent()) {
            assertThat(content).hasContentEqualTo(expextedContent());
            verify(getRequestedFor(urlEqualTo(TEST_FILENAME)));
        }

        try (
                Downloader downloader = downloaderFactory.getDownloader(new URI(URL_UNDER_TEST));
                InputStream content = downloader.getContent()) {
            verify(getRequestedFor(urlEqualTo(TEST_FILENAME))
                    .withHeader(IF_NONE_MATCH, matching("19cfad1-4ff6c5a659440")));
            assertThat(content).hasContentEqualTo(expextedContent());
        }
    }

    private void stubFileToDownload() {
        stubFor(get(urlEqualTo(TEST_FILENAME))
                .willReturn(aResponse()
                        .withHeader(LAST_MODIFIED, "Wed, 30 Jul 2014 17:19:05 GMT")
                        .withHeader(ETAG, "19cfad1-4ff6c5a659440")
                        .withStatus(200)
                        .withBody(FILE_CONTENT)));

        stubFor(get(urlEqualTo(TEST_FILENAME))
                .withHeader(IF_NONE_MATCH, matching("19cfad1-4ff6c5a659440"))
                .willReturn(aResponse()
                        .withHeader(LAST_MODIFIED, "Wed, 30 Jul 2014 17:19:05 GMT")
                        .withHeader(ETAG, "19cfad1-4ff6c5a659440")
                        .withStatus(304)));
    }

}
