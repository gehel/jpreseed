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

import ch.ledcom.jpreseed.*;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import static com.google.common.net.HttpHeaders.CONTENT_DISPOSITION;
import static java.nio.ByteBuffer.wrap;

@Controller
public class JPreseedController {

    public static final String GZIP = "application/x-gzip";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DownloaderFactory downloaderFactory;
    private final DistroService distroService;

    @Autowired(required = true)
    public JPreseedController(DownloaderFactory downloaderFactory, DistroService distroService) {
        this.downloaderFactory = downloaderFactory;
        this.distroService = distroService;
    }

    @ModelAttribute("distributions")
    public Collection<Distribution> distributions() {
        return distroService.getDistributions();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = GZIP)
    public void createUsbImage(
            @RequestParam("imageUrl") URI imageUrl,
            @RequestParam("preseeds") List<MultipartFile> preseeds,
            @RequestParam("syslinux") MultipartFile syslinux,
            HttpServletResponse response,
            OutputStream out) throws IOException {
        
        try (
                TemporaryPreseedStore preseedStore = new TemporaryPreseedStore();
                Downloader srcBootImgGz = downloaderFactory.getDownloader(imageUrl);
                GZIPOutputStream targetBootImgGz = new GZIPOutputStream(out)) {

            logger.debug("Storing preseeds...");
            preseedStore.addPreseeds(preseeds);

            logger.debug("Preparing response...");
            response.setHeader(CONTENT_DISPOSITION, "attachment; filename=\"boot.img.gz\"");

            logger.debug("Creating USB image ...");
            new UsbCreator(
                    srcBootImgGz.getContent(),
                    targetBootImgGz,
                    wrap(syslinux.getBytes()),
                    preseedStore.getPreseeds()
            ).create();
            logger.debug("USB image created.");
        }
    }

}
