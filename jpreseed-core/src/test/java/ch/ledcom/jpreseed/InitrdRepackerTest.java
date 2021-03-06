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
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import static ch.ledcom.jpreseed.TestFiles.HELLO_TXT;
import static ch.ledcom.jpreseed.TestFiles.HELLO_WORLD_TXT;
import static ch.ledcom.assertj.LedcomAssertions.assertThat;

public class InitrdRepackerTest {

    @Test
    public final void repackWithoutAdditionalFiles() throws IOException {
        try (
                InputStream initrdGz = InitrdRepackerTest.class.getResourceAsStream("/cpio-test-archive.gz");
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            new InitrdRepacker(initrdGz).repack(out);

            assertThat(gzippedCpio(out)).hasSingleEntry("hello.txt");
        }
    }

    @Test
    public final void addedFileIsPresentInArchive() throws IOException {
        try (
                InputStream initrdGz = InitrdRepackerTest.class.getResourceAsStream("/cpio-test-archive.gz");
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            new InitrdRepacker(initrdGz)
                    .addFile("hello_world.txt", HELLO_WORLD_TXT.toFile())
                    .repack(out);

            assertThat(gzippedCpio(out)).hasSingleEntry("hello_world.txt");
        }
    }

    @Test
    public final void addingExistingFileDoesNotCreateDuplicateEntries() throws IOException {
        try (
                InputStream initrdGz = InitrdRepackerTest.class.getResourceAsStream("/cpio-test-archive.gz");
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            new InitrdRepacker(initrdGz)
                    .addFile("hello.txt", HELLO_TXT.toFile())
                    .repack(out);


            assertThat(gzippedCpio(out)).hasSingleEntry("hello.txt");
        }
    }

    private static CpioArchiveInputStream gzippedCpio(ByteArrayOutputStream stream) throws IOException {
        return new CpioArchiveInputStream(new GZIPInputStream(new ByteArrayInputStream(stream.toByteArray())));
    }

}
