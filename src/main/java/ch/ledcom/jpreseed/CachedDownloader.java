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

import com.github.axet.wget.WGet;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachedDownloader {

    private final CacheNaming cacheNaming;

    public CachedDownloader(CacheNaming cacheNaming) {
        this.cacheNaming = cacheNaming;
    }

    public final Path download(URI uri) throws IOException {
        // TODO: handle failed / partial download and restarts
        Path target = cacheNaming.toPath(uri);
        if (!Files.exists(target)) {
            doDownload(uri, target);
        }
        return target;
    }

    private void doDownload(URI uri, Path target) throws IOException {
        WGet wget = new WGet(uri.toURL(), target.toFile());
        wget.download(new AtomicBoolean(false), new DownloadReporter(wget));
    }

}
