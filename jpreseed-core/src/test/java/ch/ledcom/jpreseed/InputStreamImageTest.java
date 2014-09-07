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

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class InputStreamImageTest {

    @Test
    public void resourcesAreClosedWhenImageIsClosed() throws IOException {
        InputStream in = mock(InputStream.class);
        InputStreamImage inputImage = new InputStreamImage(in);

        inputImage.getContent();

        inputImage.close();

        verify(in).close();
    }

    @Test
    public void resourcesAreNotClosedWhenImageIsNotClosed() throws IOException {
        InputStream in = mock(InputStream.class);
        InputStreamImage inputImage = new InputStreamImage(in);

        assertThat(inputImage.getContent()).isEqualTo(in);

        verify(in, never()).close();
    }
}
