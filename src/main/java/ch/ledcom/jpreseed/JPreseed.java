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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JPreseed {

    public static final String JPRESEED_DIR = ".jpreseed";

    public static void main(String[] args) throws URISyntaxException, IOException {
        URI IMAGE_URL = new URI("http://archive.ubuntu.com/ubuntu/dists/trusty-updates/main/installer-amd64/current/images/netboot/boot.img.gz");

        String homeDir = System.getProperty("user.home");

        Path cacheDirectory = Paths.get(homeDir, JPRESEED_DIR, "cache");

        CachedDownloader cachedDownloader = new CachedDownloader(new CacheNaming(cacheDirectory));
        new UsbCreator(cachedDownloader, IMAGE_URL).create();
    }

}
