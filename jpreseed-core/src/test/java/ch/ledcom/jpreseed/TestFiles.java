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

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFiles {
    public static final Path HELLO_TXT = Paths.get("src/test/resources/hello.txt");
    public static final Path HELLO_WORLD_TXT = Paths.get("src/test/resources/hello_world.txt");
    public static final Path VFAT_IMG_GZ = Paths.get("src/test/resources/vfat.img.gz");
    public static final Path NEW_IMAGE = Paths.get("target/test-vfat.img.gz");

    public static File helloTxtFile() {
        URL resource = TestFiles.class.getResource("/hello.txt");
        return new File(resource.getFile());
    }

    public static Path helloTxtPath() {
        return helloTxtFile().toPath();
    }

}
