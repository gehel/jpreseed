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
package ch.ledcom.jpreseed.web;


import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static com.google.common.base.Charsets.UTF_8;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static ch.ledcom.assertj.LedcomAssertions.assertThat;

public class TemporaryPreseedStoreTest {

    public static final String PRESEED_CONTENT = "hello world";
    private MultipartFile testPreseed;

    @Before
    public void initTestPreseed() throws IOException {
        testPreseed = mock(MultipartFile.class);
        when(testPreseed.getName()).thenReturn("filename.ext");
        when(testPreseed.getInputStream()).thenReturn(dummyInputStream());
    }

    @Test
    public void preseedsAreStoredOnDisk() throws IOException {
        try (TemporaryPreseedStore preseedStore = new TemporaryPreseedStore()) {
            preseedStore.addPreseeds(singleton(testPreseed));

            ImmutableSet<Path> preseeds = preseedStore.getPreseeds();
            assertThat(preseeds).hasSize(1);

            Path preseed = preseeds.iterator().next();

            assertThat(preseed).hasFileName("filename.ext");
            assertThat(preseed).hasContent(PRESEED_CONTENT);
        }
    }

    @Test
    public void preseedsAreDeletedAfterOnClose() throws IOException {
        Path preseed;
        try (TemporaryPreseedStore preseedStore = new TemporaryPreseedStore()) {
            preseedStore.addPreseeds(singleton(testPreseed));

            ImmutableSet<Path> preseeds = preseedStore.getPreseeds();
            preseed = preseeds.iterator().next();
        }
        assertThat(preseed).doesNotExist();
        assertThat(preseed.getParent()).doesNotExist();
    }

    private InputStream dummyInputStream() {
        return new ByteArrayInputStream(PRESEED_CONTENT.getBytes(UTF_8));
    }
}
