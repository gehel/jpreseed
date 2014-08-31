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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.google.common.io.ByteStreams.copy;

/**
 * WARNING: this repacker will only add files at the root of the archive. This
 * is sufficient for the purpose required by this project, but is not enough
 * for a generic repacker ...
 */
public class InitrdRepacker {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final InputStream initrdGz;

    private final Map<String, File> additionalFiles = new HashMap<>();

    public InitrdRepacker(InputStream initrdGz) {
        this.initrdGz = initrdGz;
    }


    public final InitrdRepacker addFile(String name, File content) {
        additionalFiles.put(name, content);
        return this;
    }

    public final void repack(OutputStream out) throws IOException {
        // start new archive
        try (
                CpioArchiveInputStream cpioIn = new CpioArchiveInputStream(new GZIPInputStream(initrdGz));
                CpioArchiveOutputStream cpioOut = new CpioArchiveOutputStream(new GZIPOutputStream(out))) {
            CpioArchiveEntry cpioEntry;

            // add files from base archive
            while ((cpioEntry = cpioIn.getNextCPIOEntry()) != null) {
                if (!additionalFiles.keySet().contains(cpioEntry.getName())) {
                    logger.info("Repacking [{}]", cpioEntry.getName());
                    cpioOut.putArchiveEntry(cpioEntry);
                    long bytesCopied = copy(cpioIn, cpioOut);
                    cpioOut.closeArchiveEntry();
                    logger.debug("Copied [{}] bytes", bytesCopied);
                }
            }

            // additional files
            for (Map.Entry<String, File> entry : additionalFiles.entrySet()) {
                logger.info("Packing new file [{}]", entry.getKey());
                ArchiveEntry additionalEntry = cpioOut.createArchiveEntry(entry.getValue(), entry.getKey());
                cpioOut.putArchiveEntry(additionalEntry);
                try (InputStream in = new FileInputStream(entry.getValue())) {
                    copy(in, cpioOut);
                }
                cpioOut.closeArchiveEntry();
            }
        }
    }
}
