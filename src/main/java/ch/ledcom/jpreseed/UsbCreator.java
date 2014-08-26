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

import com.google.common.base.Function;
import com.google.common.io.ByteBufferDataInputStream;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;

public class UsbCreator {

    private static final String INITRD_GZ = "initrd.gz";
    private static final String SYSLINUX_CFG = "syslinux.cfg";
    private final Path srcBootImgGz;
    private final Path targetBootImg;
    private final Path syslinuxcfg;
    private final Set<Path> preseedFiles;
    private final PathToFile toFile = new PathToFile();

    public UsbCreator(Path srcBootImgGz, Path targetBootImg, Path syslinuxcfg, Set<Path> preseedFiles) {
        this.srcBootImgGz = srcBootImgGz;
        this.targetBootImg = targetBootImg;
        this.syslinuxcfg = syslinuxcfg;
        this.preseedFiles = preseedFiles;
    }

    public final void create() throws IOException {

        try (FatModifier fatModifier = new FatModifier(srcBootImgGz)) {

            try (FileChannel syslinuxcfgChannel = FileChannel.open(syslinuxcfg)) {
                ByteBuffer syslinuxcfgBuffer = syslinuxcfgChannel.map(READ_ONLY, 0, syslinuxcfgChannel.size());

                fatModifier.addOrReplace(INITRD_GZ, repackedInitrd(fatModifier.getFileContent(INITRD_GZ)));
                fatModifier.addOrReplace(SYSLINUX_CFG, syslinuxcfgBuffer);
            }

            fatModifier.flush();

            writeTargetBootImg(fatModifier.getByteBuffer());
        }
    }

    private void writeTargetBootImg(ByteBuffer bootImgBuffer) throws IOException {
        try (FileChannel targetChannel = FileChannel.open(targetBootImg, CREATE_NEW, WRITE)) {
            targetChannel.write(bootImgBuffer);
        }
    }

    private ByteBuffer repackedInitrd(ByteBuffer srcInitrdGz) throws IOException {
        ByteArrayOutputStream newInitrd = new ByteArrayOutputStream();
        new InitrdRepacker(new ByteBufferDataInputStream(srcInitrdGz))
                .addFiles(transform(preseedFiles, toFile))
                .repack(newInitrd);

        return ByteBuffer.wrap(newInitrd.toByteArray());
    }

    private static class PathToFile implements Function<Path, File> {
        @Nonnull
        @Override
        public File apply(@Nonnull Path input) {
            return input.toFile();
        }
    }
}
