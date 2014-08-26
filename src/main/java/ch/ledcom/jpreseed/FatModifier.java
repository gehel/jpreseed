package ch.ledcom.jpreseed;

import de.waldheinz.fs.FileSystem;
import de.waldheinz.fs.FileSystemFactory;
import de.waldheinz.fs.FsDirectoryEntry;
import de.waldheinz.fs.FsFile;
import de.waldheinz.fs.util.RamDisk;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import static java.lang.String.format;

public class FatModifier implements Closeable {

    private final FileSystem fileSystem;
    private final RamDisk ramDisk;

    public FatModifier(Path fsPath) throws IOException {
        ramDisk = RamDisk.readGzipped(fsPath.toFile());
        this.fileSystem = FileSystemFactory.create(ramDisk, false);
    }

    public final ByteBuffer getFileContent(String fileName) throws IOException {
        FsFile file = fileSystem.getRoot().getEntry(fileName).getFile();
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

    public final ByteBuffer getByteBuffer() {
        return ramDisk.getBuffer();
    }

    @Override
    public final void close() throws IOException {
        fileSystem.close();
    }

    public void flush() throws IOException {
        fileSystem.flush();
    }
}
