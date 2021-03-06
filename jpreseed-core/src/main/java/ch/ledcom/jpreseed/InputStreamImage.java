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

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.io.InputStream;

@NotThreadSafe
public class InputStreamImage implements InputImage {
    private final InputStream inputStream;
    private final Closer closer = Closer.create();

    public InputStreamImage(InputStream inputStream) {
        this.inputStream = closer.register(inputStream);
    }

    @Override
    public final InputStream getContent() {
        return inputStream;
    }

    @Override
    public final void close() throws IOException {
        closer.close();
    }
}
