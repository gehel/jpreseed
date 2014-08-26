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

import ch.ledcom.jpreseed.utils.DeletingFileVisitor;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.annotations.VisibleForTesting;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CachedDownloaderTest {
    private static final int HTTP_PORT = 9999;
    private static final String TEST_FILENAME = "/test.txt";
    private static final String URL_UNDER_TEST = "http://localhost:" + HTTP_PORT + TEST_FILENAME;
    private static final String FILE_CONTENT = "Hello world !";
    private static final Path CACHE_DIRECTORY = Paths.get("target/test-downloads");

    @Rule
    @VisibleForTesting
    public WireMockRule wireMockRule = new WireMockRule(HTTP_PORT);

    private CachedDownloader downloader;

    @Before
    public final void createDownloader() {
        downloader = new CachedDownloader(new CacheNaming(CACHE_DIRECTORY));
    }

    @Test
    public final void downloadCreateFileOnDisk() throws URISyntaxException, IOException {
        stubFileToDownload();

        Path downloadedFile = downloader.download(new URI(URL_UNDER_TEST));
        assertThat(downloadedFile.toFile()).hasContent(FILE_CONTENT);

        verify(getRequestedFor(urlEqualTo(TEST_FILENAME)));
    }

    @Test
    public final void secondDownloadGetsFileFromCache() throws URISyntaxException, IOException {
        stubFileToDownload();

        Path downloadedFile = downloader.download(new URI(URL_UNDER_TEST));
        assertThat(downloadedFile.toFile()).hasContent(FILE_CONTENT);
        verify(getRequestedFor(urlEqualTo(TEST_FILENAME)));

        int numberOfRequestsAfterFirstCall = wireMockRule.findAll(getRequestedFor(urlEqualTo(TEST_FILENAME))).size();

        downloadedFile = downloader.download(new URI(URL_UNDER_TEST));

        assertThat(downloadedFile.toFile()).hasContent(FILE_CONTENT);
        int numberOfRequestsAfterSecondCall = wireMockRule.findAll(getRequestedFor(urlEqualTo(TEST_FILENAME))).size();

        assertThat(numberOfRequestsAfterSecondCall).isEqualTo(numberOfRequestsAfterFirstCall);
    }

    private void stubFileToDownload() {
        stubFor(get(urlEqualTo(TEST_FILENAME))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(FILE_CONTENT)));
    }

    @Before
    @After
    public final void removeDownloadDirectory() throws IOException {
        if (Files.exists(CACHE_DIRECTORY)) {
            Files.walkFileTree(CACHE_DIRECTORY, new DeletingFileVisitor());
        }
    }

}
