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
package ch.ledcom.jpreseed.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.delete;

public class DeletingFileVisitor extends SimpleFileVisitor<Path> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
        if (attributes.isRegularFile()) {
            logger.debug("Deleting Regular File: [{}]", file.getFileName());
            delete(file);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path directory, IOException ioe) throws IOException {
        logger.debug("Deleting Directory: [{}]", directory.getFileName());
        delete(directory);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException ioe) throws IOException {
        logger.error("Something went wrong while working on : [{}]", file.getFileName(), ioe);
        return CONTINUE;
    }
}
