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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Function;

import javax.annotation.Nonnull;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.transform;

public class JPreseedArguments {

    public static final String SOURCE_FILE = "--sourceFile";
    public static final String SOURCE_URL = "--sourceUrl";
    public static final String TARGET_FILE = "--targetFile";
    public static final String SYSCONFIG_FILE = "--sysconfigFile";
    public static final String PRESEEDS = "--preseeds";
    public static final String HELP = "--help";

    @Parameter(
            names = SOURCE_FILE,
            description = "Source image (ISO or USB image downloaded from your distribution)",
            converter = PathConverter.class,
            validateValueWith = ExistingPathValidator.class)
    private Path sourceFile;

    @Parameter(
            names = SOURCE_URL,
            description = "URL at which to download the source image",
            converter = URIConverter.class)
    private URI sourceUrl;

    @Parameter(
            names = TARGET_FILE,
            description = "Where to create the new repackaged image",
            converter = PathConverter.class,
            validateValueWith = NonExistingPathValidator.class,
            required = true)
    private Path targetImage;

    @Parameter(
            names = SYSCONFIG_FILE,
            description = "the sysconfig file to repackage in the new image",
            converter = PathConverter.class,
            validateValueWith = ExistingPathValidator.class,
            required = true)
    private Path sysConfigFile;

    @Parameter(
            names = PRESEEDS,
            description = "Your custom preseed files",
            variableArity = true,
            required = true)
    private List<String> preseeds;

    @Parameter(names = HELP, help = true)
    private boolean help;

    public final Path getSourceFile() {
        return sourceFile;
    }

    public final URI getSourceUrl() {
        return sourceUrl;
    }

    public final Path getTargetImage() {
        return targetImage;
    }

    public final Path getSysConfigFile() {
        return sysConfigFile;
    }

    public final Collection<Path> getPreseeds() {
        return transform(preseeds, new StringToPath());
    }

    public final boolean isHelp() {
        return help;
    }

    public final void validate() {
        if ((getSourceFile() != null) && (getSourceUrl() != null)) {
            throw new ParameterException("Cannot give both source file and source URL.");
        }
        if ((getSourceFile() == null) && (getSourceUrl() == null)) {
            throw new ParameterException("Need to give source file or source URL.");
        }
    }

    private static class StringToPath implements Function<String, Path> {
        @Nonnull
        @Override
        public Path apply(@Nonnull String input) {
            return Paths.get(input);
        }
    }

}
