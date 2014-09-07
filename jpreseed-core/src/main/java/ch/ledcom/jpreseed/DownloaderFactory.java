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

import org.apache.http.impl.client.CloseableHttpClient;

import javax.annotation.concurrent.ThreadSafe;
import java.net.URI;

@ThreadSafe
public class DownloaderFactory {

    private final CloseableHttpClient httpClient;

    public DownloaderFactory(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public final Downloader getDownloader(URI uri) {
        return new Downloader(httpClient, uri);
    }
}
