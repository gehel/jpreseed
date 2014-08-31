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

import com.google.common.io.ByteBufferDataInputStream;
import de.waldheinz.fs.FileSystem;
import de.waldheinz.fs.FileSystemFactory;
import de.waldheinz.fs.FsDirectoryEntry;
import de.waldheinz.fs.FsFile;
import de.waldheinz.fs.util.RamDisk;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static ch.ledcom.jpreseed.TestFiles.*;
import static ch.ledcom.jpreseed.UsbCreator.INITRD_GZ;
import static ch.ledcom.jpreseed.UsbCreator.SYSLINUX_CFG;
import static ch.ledcom.assertj.LedcomAssertions.assertThat;

public class UsbCreatorTest {

    @Test
    public void createUsb() throws IOException {
        Date startTime = new Date();
        ByteBuffer sysLinuxCfg = ByteBuffer.wrap("sysLinuxCfg".getBytes());
        Set<Path> preseeds = Collections.singleton(HELLO_WORLD_TXT);

        try (InputStream srcBootImg = Files.newInputStream(VFAT_IMG_GZ);
             GZIPOutputStream newBootImg = new GZIPOutputStream(Files.newOutputStream(NEW_IMAGE))) {
            UsbCreator usbCreator = new UsbCreator(srcBootImg, newBootImg, sysLinuxCfg, preseeds);
            usbCreator.create();
        }

        assertThat(NEW_IMAGE).exists();

        FileSystem fileSystem = FileSystemFactory.create(RamDisk.readGzipped(NEW_IMAGE.toFile()), true);

        FsDirectoryEntry newInitRdGzEntry = fileSystem.getRoot().getEntry(INITRD_GZ);
        assertThat(newInitRdGzEntry).hasBeenModifiedAt(startTime, 2000);
        assertThat(fileSystem.getRoot().getEntry(SYSLINUX_CFG)).hasBeenModifiedAt(startTime, 2000);

        CpioArchiveInputStream initRdCpio = getCpioArchiveInputStream(newInitRdGzEntry);
        assertThat(initRdCpio).hasSingleEntry(HELLO_WORLD_TXT.getFileName().toString());
    }

    private CpioArchiveInputStream getCpioArchiveInputStream(FsDirectoryEntry newInitRdGzEntry) throws IOException {
        FsFile initrdGzFile = newInitRdGzEntry.getFile();
        ByteBuffer initRdBuffer = ByteBuffer.allocate((int) initrdGzFile.getLength());
        initrdGzFile.read(0, initRdBuffer);
        initRdBuffer.rewind();
        return new CpioArchiveInputStream(new GZIPInputStream(new ByteBufferDataInputStream(initRdBuffer)));
    }

    @Before
    @After
    public void deleteNewBootImg() throws IOException {
        Files.deleteIfExists(NEW_IMAGE);
    }

}
