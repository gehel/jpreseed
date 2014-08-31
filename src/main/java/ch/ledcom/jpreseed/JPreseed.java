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

import ch.ledcom.jpreseed.cli.JPreseedArguments;
import com.beust.jcommander.JCommander;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

public class JPreseed {

    public static final String JPRESEED_DIR = ".jpreseed";
    private final CachedDownloader downloader;

    public JPreseed() {
        String homeDir = System.getProperty("user.home");
        Path cacheDirectory = Paths.get(homeDir, JPRESEED_DIR, "cache");
        downloader = new CachedDownloader(new CacheNaming(cacheDirectory));
    }

    public final void create(JPreseedArguments arguments) throws IOException {
        try (
                InputStream image = Files.newInputStream(getSourceImage(arguments));
                GZIPOutputStream newImage = new GZIPOutputStream(Files.newOutputStream(arguments.getTargetImage()))) {
            ByteBuffer sysConfigCfg = ByteBuffer.wrap(Files.readAllBytes(arguments.getSysConfigFile()));
            new UsbCreator(
                    image,
                    newImage,
                    sysConfigCfg,
                    arguments.getPreseeds()
            ).create();
        }
    }

    private Path getSourceImage(JPreseedArguments arguments) throws IOException {
        if (arguments.getSourceUrl() != null) {
            return downloader.download(arguments.getSourceUrl());
        } else {
            return arguments.getSourceFile();
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        JPreseedArguments arguments = new JPreseedArguments();
        JCommander jCommander = new JCommander(arguments, args);
        if (arguments.isHelp()) {
            jCommander.usage();
            System.exit(0);
        }
        arguments.validate();
        new JPreseed().create(arguments);
    }

}
