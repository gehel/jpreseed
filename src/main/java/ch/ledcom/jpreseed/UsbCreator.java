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

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteBufferDataInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;

import static com.google.common.collect.ImmutableSet.copyOf;

public class UsbCreator {

    public static final String INITRD_GZ = "initrd.gz";
    public static final String SYSLINUX_CFG = "syslinux.cfg";
    private final InputStream srcBootImgGz;
    private final OutputStream targetBootImg;
    private final ByteBuffer sysLinuxCfg;
    private final ImmutableSet<Path> preseedFiles;

    public UsbCreator(InputStream srcBootImgGz, GZIPOutputStream targetBootImg, ByteBuffer sysLinuxCfg, Collection<Path> preseedFiles) {
        this.srcBootImgGz = srcBootImgGz;
        this.targetBootImg = targetBootImg;
        this.sysLinuxCfg = sysLinuxCfg;
        this.preseedFiles = copyOf(preseedFiles);
    }

    public final void create() throws IOException {
        try (FatModifier fatModifier = new FatModifier(srcBootImgGz)) {
            ByteBuffer srcInitrdGz = fatModifier.getFileContent(INITRD_GZ);
            srcInitrdGz.rewind();
            fatModifier.addOrReplace(INITRD_GZ, repackedInitrd(srcInitrdGz));
            fatModifier.addOrReplace(SYSLINUX_CFG, sysLinuxCfg);

            fatModifier.flush();

            targetBootImg.write(fatModifier.getByteBuffer().array());
        }
    }

    private ByteBuffer repackedInitrd(ByteBuffer srcInitrdGz) throws IOException {
        ByteArrayOutputStream newInitrd = new ByteArrayOutputStream();
        InitrdRepacker initrdRepacker = new InitrdRepacker(new ByteBufferDataInputStream(srcInitrdGz));
        for (Path preseedFile : preseedFiles) {
            initrdRepacker.addFile(preseedFile.getFileName().toString(), preseedFile.toFile());
        }
        initrdRepacker.repack(newInitrd);

        return ByteBuffer.wrap(newInitrd.toByteArray());
    }

}
