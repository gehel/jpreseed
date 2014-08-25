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

import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

import static ch.ledcom.jpreseed.assertions.MyAssertions.assertThat;
import static java.util.Collections.singleton;

public class InitrdRepackerTest {

    private final File repackedArchiveFile = new File("cpio-repacked.gz");

    @Test
    public void repackWithoutAdditionalFiles() throws IOException {
        try (
                InputStream initrdGz = InitrdRepackerTest.class.getResourceAsStream("/cpio-test-archive.gz");
                FileOutputStream out = new FileOutputStream(repackedArchiveFile)) {
            new InitrdRepacker(initrdGz).repack(out);
        }

        assertThat(gzippedCpio(repackedArchiveFile)).hasEntry("hello.txt");
    }

    @Test
    public void addedFileIsPresentInArchive() throws IOException {
        try (
                InputStream initrdGz = InitrdRepackerTest.class.getResourceAsStream("/cpio-test-archive.gz");
                FileOutputStream out = new FileOutputStream(repackedArchiveFile)) {
            File fileToAdd = new File("src/test/resources/hello_world.txt");
            new InitrdRepacker(initrdGz)
                    .addFiles(singleton(fileToAdd))
                    .repack(out);
        }

        assertThat(gzippedCpio(repackedArchiveFile)).hasEntry("hello_world.txt");
    }

    @Before
    @After
    public void removedRepackedArchive() {
        repackedArchiveFile.delete();
    }

    private static CpioArchiveInputStream gzippedCpio(File file) throws IOException {
        return new CpioArchiveInputStream(new GZIPInputStream(new FileInputStream(file)));
    }

}
