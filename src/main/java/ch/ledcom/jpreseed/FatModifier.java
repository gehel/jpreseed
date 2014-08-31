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

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static java.lang.String.format;

public class FatModifier implements Closeable {

    private final FileSystem fileSystem;
    private final RamDisk ramDisk;

    public FatModifier(InputStream gzippedFat) throws IOException {
        ramDisk = RamDisk.readGzipped(gzippedFat);
        this.fileSystem = FileSystemFactory.create(ramDisk, false);
    }

    public final ByteBuffer getFileContent(String fileName) throws IOException {
        FsDirectoryEntry entry = fileSystem.getRoot().getEntry(fileName);
        if (entry == null) {
            throw new FileNotFoundException(fileName);
        }
        if (!entry.isFile()) {
            throw new IOException(String.format("Can only get content of files and [%s] is not a file.", fileName));
        }
        FsFile file = entry.getFile();
        ByteBuffer buffer = ByteBuffer.allocate((int) file.getLength());
        file.read(0, buffer);
        return buffer;
    }

    public final void addOrReplace(String name, ByteBuffer content) throws IOException {
        FsDirectoryEntry entry = fileSystem.getRoot().getEntry(name);
        if (entry == null) {
            entry = fileSystem.getRoot().addFile(name);
        }
        if (!entry.isFile()) {
            throw new IllegalStateException(format("Entry [%s] already exists and is a directory, not a file.", name));
        }
        entry.getFile().write(0, content);
    }

    public final ByteBuffer getByteBuffer() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate((int) ramDisk.getSize());
        ramDisk.read(0, buffer);
        return buffer;
    }

    @Override
    public final void close() throws IOException {
        fileSystem.close();
    }

    public final boolean isClosed() {
        return fileSystem.isClosed();
    }

    public final void flush() throws IOException {
        fileSystem.flush();
    }
}
