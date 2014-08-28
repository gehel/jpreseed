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

import de.waldheinz.fs.FileSystem;
import de.waldheinz.fs.FileSystemFactory;
import de.waldheinz.fs.FsDirectoryEntry;
import de.waldheinz.fs.FsFile;
import de.waldheinz.fs.util.RamDisk;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

import static ch.ledcom.jpreseed.assertions.MyAssertions.assertThat;

public class FatModifierTest {

    private FatModifier fatModifier;

    @Before
    public final void initFatModifier() throws IOException {
        fatModifier = new FatModifier(Paths.get("src/test/resources/vfat.img.gz"));
    }

    @Test
    public final void existingFileCanBeFound() throws IOException {
        assertThat(fatModifier.getFileContent("hello.txt")).hasContent("hello\n");
    }

    @Test
    public final void newFileCanBeAdded() throws IOException {
        ByteBuffer newContent = ByteBuffer.wrap("new content".getBytes());

        fatModifier.addOrReplace("new_content.txt", newContent);
        fatModifier.flush();
        fatModifier.close();

        ByteBuffer fatBuffer = fatModifier.getByteBuffer();

        FileSystem fs = createFileSystem(fatBuffer);

        assertThat(getFileContent(fs, "new_content.txt")).hasContent("new content");
    }

    @Test
    public final void existingFileCanBeModified() throws IOException {
        ByteBuffer newContent = ByteBuffer.wrap("new content".getBytes());

        assertThat(fatModifier.getFileContent("hello.txt")).hasContent("hello\n");

        fatModifier.addOrReplace("hello.txt", newContent);
        fatModifier.flush();
        fatModifier.close();

        ByteBuffer fatBuffer = fatModifier.getByteBuffer();

        FileSystem fs = createFileSystem(fatBuffer);

        assertThat(getFileContent(fs, "hello.txt")).hasContent("new content");
    }

    @Test(expected = IllegalStateException.class)
    public final void replacingDirectoryWithFileIsNotPossible() throws IOException {
        ByteBuffer newContent = ByteBuffer.wrap("new content".getBytes());

        try {
            fatModifier.addOrReplace("my_dir", newContent);
        } catch (IllegalStateException ise) {
            assertThat(ise.getMessage()).contains("my_dir");
            throw ise;
        }
    }

    private FileSystem createFileSystem(ByteBuffer buffer) throws IOException {
        RamDisk ramDisk = new RamDisk(buffer.limit());
        buffer.rewind();
        ramDisk.write(0, buffer);
        return FileSystemFactory.create(ramDisk, true);
    }

    private ByteBuffer getFileContent(FileSystem fs, String filename) throws IOException {
        FsDirectoryEntry entry = fs.getRoot().getEntry(filename);
        assertThat(entry).isNotNull();
        assertThat(entry.isFile()).isTrue();

        FsFile file = entry.getFile();
        assertThat(file).isNotNull();
        ByteBuffer buffer = ByteBuffer.allocate((int) file.getLength());
        file.read(0, buffer);
        return buffer;
    }

}
