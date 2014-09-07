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

import com.google.common.io.Closer;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@NotThreadSafe
public class Downloader implements Closeable {

    private final CloseableHttpClient httpClient;
    private final URI uri;
    private final Closer closer = Closer.create();

    public Downloader(CloseableHttpClient httpClient, URI uri) {
        this.httpClient = httpClient;
        this.uri = uri;
    }

    public InputStream getContent() throws IOException {
        return closer.register(httpClient.execute(new HttpGet(uri)).getEntity().getContent());
    }

    @Override
    public void close() throws IOException {
        closer.close();
    }
}
