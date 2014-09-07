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
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DownloadedInputImageTest {

    @Mock private InputStream inputStream;
    @Mock private Downloader downloader;

    @Before
    public void initMockito() throws IOException {
        initMocks(this);
        doReturn(inputStream).when(downloader).getContent();
    }

    @Test
    public void resourcesAreClosedWhenImageIsClosed() throws IOException {
        DownloadedInputImage image = new DownloadedInputImage(downloader);

        image.getContent();

        image.close();

        verify(inputStream).close();
        verify(downloader).close();
    }

    @Test
    public void resourcesAreNotClosedWhenImageIsNotClosed() throws IOException {
        DownloadedInputImage image = new DownloadedInputImage(downloader);

        image.getContent();

        verify(inputStream, never()).close();
        verify(downloader, never()).close();
    }

}
