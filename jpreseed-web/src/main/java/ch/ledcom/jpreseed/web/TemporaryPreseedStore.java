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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@NotThreadSafe
public class TemporaryPreseedStore implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Path tempDir;
    private final Set<Path> preseedPaths = new HashSet<>();

    public TemporaryPreseedStore() throws IOException {
        tempDir = Files.createTempDirectory("bootImage");
    }

    public ImmutableSet<Path> getPreseeds() {
        return copyOf(preseedPaths);
    }

    public void addPreseeds(Collection<MultipartFile> preseeds) throws IOException {
        for (MultipartFile multipartFile : preseeds) {
            String name = multipartFile.getName();
            logger.debug("Adding pressed [{}] to preseed store.", name);
            Path preseed = tempDir.resolve(name);
            preseedPaths.add(preseed);
            try (InputStream in = multipartFile.getInputStream()) {
                Files.copy(in, preseed, REPLACE_EXISTING);
            }
        }
    }

    private void deletePreseeds() {
        for (Path preseed : preseedPaths) {
            try {
                logger.debug("Deleting preseed file [{}].", preseed);
                Files.deleteIfExists(preseed);
            } catch (IOException ioe) {
                logger.error("Could not delete preseed file", ioe);
            }
        }
    }

    @Override
    public void close() {
        deletePreseeds();
        try {
            logger.debug("Deleting temp dir [{}].", tempDir);
            Files.deleteIfExists(tempDir);
        } catch (IOException ioe) {
            logger.error("Could not delete temp dir [{}].", tempDir, ioe);
        }
    }

}
