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

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheNamingTest {

    private CacheNaming cacheNaming;
    private static final Path CACHE_DIRECTORY = Paths.get("/cacheDirectory");

    @Before
    public void initializeCacheNaming() {
        cacheNaming = new CacheNaming(CACHE_DIRECTORY);
    }

    @Test
    public void testSimpleUrl() throws URISyntaxException {
        assertThat(cacheNaming.toPath(new URI("http://test.net/path/to/file.ext")).toString())
                .isEqualTo("/cacheDirectory/test.net/path/to/file.ext");
    }

    @Test
    public void testHostOnly() throws URISyntaxException {
        assertThat(cacheNaming.toPath(new URI("http://test.net")).toString())
                .isEqualTo("/cacheDirectory/test.net");
    }

    @Test
    public void testHostWithOnlyRootSlash() throws URISyntaxException {
        assertThat(cacheNaming.toPath(new URI("http://test.net/")).toString())
                .isEqualTo("/cacheDirectory/test.net");
    }

    @Test
    public void testFileOnly() throws URISyntaxException {
        assertThat(cacheNaming.toPath(new URI("http://test.net/file.ext")).toString())
                .isEqualTo("/cacheDirectory/test.net/file.ext");
    }

    @Test
    public void testWithParameters() throws URISyntaxException {
        assertThat(cacheNaming.toPath(new URI("http://test.net/file.ext?param=value")).toString())
                .isEqualTo("/cacheDirectory/test.net/file.ext/param=value");
    }

    @Test
    public void testWithFragments() throws URISyntaxException {
        assertThat(cacheNaming.toPath(new URI("http://test.net/file.ext#toto")).toString())
                .isEqualTo("/cacheDirectory/test.net/file.ext/toto");
    }

}
