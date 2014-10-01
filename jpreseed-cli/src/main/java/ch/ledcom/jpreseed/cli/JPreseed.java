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
package ch.ledcom.jpreseed.cli;

import ch.ledcom.jpreseed.*;
import ch.ledcom.jpreseed.cli.args.JPreseedArguments;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.zip.GZIPOutputStream;

public class JPreseed {

    private static final int MAX_CACHE_ENTRIES = 20;
    private static final int MAX_OBJECT_SIZE = 100 * 1024 * 1024;
    private static final int CONNECT_TIMEOUT = 1000;
    private static final int SOCKET_TIMEOUT = 1000;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DownloaderFactory downloaderFactory;
    private final UsbCreator usbCreator;

    public JPreseed(DownloaderFactory downloaderFactory, UsbCreator usbCreator) {
        this.downloaderFactory = downloaderFactory;
        this.usbCreator = usbCreator;
    }

    public final void create(JPreseedArguments arguments) throws IOException {
        try (
                InputImage image = getSourceImage(arguments);
                GZIPOutputStream newImage = new GZIPOutputStream(Files.newOutputStream(arguments.getTargetImage()))) {
            ByteBuffer sysConfigCfg = ByteBuffer.wrap(Files.readAllBytes(arguments.getSysConfigFile()));
            usbCreator.create(
                    image.getContent(),
                    newImage,
                    sysConfigCfg,
                    arguments.getPreseeds()
            );
        }
    }

    private InputImage getSourceImage(final JPreseedArguments arguments) throws IOException {
        if (arguments.getSourceUrl() != null) {
            logger.info("Downloading image from [{}]", arguments.getSourceUrl());
            return new DownloadedInputImage(downloaderFactory.getDownloader(arguments.getSourceUrl()));
        } else {
            return new InputStreamImage(Files.newInputStream(arguments.getSourceFile()));
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        JPreseedArguments arguments = new JPreseedArguments();
        try {
            JCommander jCommander = new JCommander(arguments, args);
            if (arguments.isHelp()) {
                jCommander.usage();
                System.exit(0);
            }
            arguments.validate();
            initialize().create(arguments);
        } catch (ParameterException pe) {
            System.err.println(pe.getMessage());
            System.exit(-1);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(-2);
        }
    }

    private static JPreseed initialize() {
        CacheConfig cacheConfig = CacheConfig.custom()
                .setMaxCacheEntries(MAX_CACHE_ENTRIES)
                .setMaxObjectSize(MAX_OBJECT_SIZE)
                .build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();

        DownloaderFactory downloaderFactory = new DownloaderFactory(CachingHttpClients.custom()
                .setCacheConfig(cacheConfig)
                .setDefaultRequestConfig(requestConfig)
                .build());
        return new JPreseed(downloaderFactory, new UsbCreator());
    }

}
