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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.zip.GZIPOutputStream;

public class JPreseed {

    public static final String JPRESEED_DIR = ".jpreseed";
    private final CachedDownloader downloader;

    public JPreseed() {
        String homeDir = System.getProperty("user.home");
        Path cacheDirectory = Paths.get(homeDir, JPRESEED_DIR, "cache");
        downloader = new CachedDownloader(new CacheNaming(cacheDirectory));
    }

    public final void create(URI imageUrl) throws IOException {
        try (
                InputStream image = Files.newInputStream(downloader.download(imageUrl));
                GZIPOutputStream newImage = new GZIPOutputStream(Files.newOutputStream(Paths.get("boot.img")))) {
            ByteBuffer sysConfigCfg = ByteBuffer.allocate(0);
            new UsbCreator(
                    image,
                    newImage,
                    sysConfigCfg,
                    Collections.<Path>emptySet()
            ).create();
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        URI imageUrl = new URI("http://archive.ubuntu.com/ubuntu/dists/trusty-updates/main/installer-amd64/current/images/netboot/boot.img.gz");
        new JPreseed().create(imageUrl);
    }

}
