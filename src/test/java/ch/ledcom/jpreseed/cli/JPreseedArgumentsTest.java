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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ch.ledcom.jpreseed.TestFiles.HELLO_TXT;
import static ch.ledcom.jpreseed.TestFiles.HELLO_WORLD_TXT;
import static ch.ledcom.jpreseed.TestFiles.NON_EXISTING;
import static ch.ledcom.jpreseed.assertions.MyAssertions.assertThat;
import static ch.ledcom.jpreseed.cli.JPreseedArguments.*;

public class JPreseedArgumentsTest {

    private JPreseedArguments arguments;

    @Before
    public void initializeArguments() {
        arguments = new JPreseedArguments();
    }

    @Test
    public void argumentsAreParsed() throws URISyntaxException {
        String[] args = new String[]{
                SOURCE_URL, "http://test.net/file.img",
                SOURCE_FILE, HELLO_TXT.toString(),
                TARGET_FILE, NON_EXISTING.toString(),
                SYSCONFIG_FILE, HELLO_WORLD_TXT.toString(),
                PRESEEDS, HELLO_TXT.toString(), HELLO_WORLD_TXT.toString()
        };

        new JCommander(arguments, args);

        assertThat(arguments.getSourceUrl()).isEqualTo(new URI("http://test.net/file.img"));
        assertThat(arguments.getSourceFile()).isEqualTo(HELLO_TXT);
        assertThat(arguments.getTargetImage()).isEqualTo(NON_EXISTING);
        assertThat(arguments.getSysConfigFile()).isEqualTo(HELLO_WORLD_TXT);
        assertThat(arguments.getPreseeds())
                .contains(HELLO_TXT)
                .contains(HELLO_WORLD_TXT);
    }

    @Test
    public void urlParsingWorks() throws URISyntaxException {
        String[] args = new String[]{SOURCE_URL, "http://test.net/file.img"};
        new JCommander(arguments, args);
        assertThat(arguments.getSourceUrl()).isEqualTo(new URI("http://test.net/file.img"));
    }

    @Test(expected = ParameterException.class)
    public void sourceUrlIsValidated() throws URISyntaxException {
        String[] args = new String[]{SOURCE_URL, "^invalid^"};
        new JCommander(arguments, args);
    }

    @Test
    public void parsingPathWorks() throws IOException {
        String[] args = new String[]{SOURCE_FILE, HELLO_TXT.toString()};
        new JCommander(arguments, args);
        assertThat(arguments.getSourceFile()).isEqualTo(HELLO_TXT);
        assertThat(arguments.getSourceFile()).exists();
    }

    @Test(expected = ParameterException.class)
    public void validationErrorIfSourceFileDoesNotExist() throws IOException {
        Path nonExistingPath = Paths.get("non-existing-file");
        assertThat(nonExistingPath).doesNotExist();
        String[] args = new String[]{SOURCE_FILE, nonExistingPath.toString()};
        new JCommander(arguments, args);
    }

    @Test(expected = ParameterException.class)
    public void validationErrorIfTargetFileDoesExist() throws IOException {
        String[] args = new String[]{TARGET_FILE, HELLO_TXT.toString()};
        new JCommander(arguments, args);
    }

    @Test(expected = ParameterException.class)
    public void needToGiveEitherSourceFileOrUrl() {
        String[] args = new String[]{};
        new JCommander(arguments, args);
        arguments.validate();
    }

    @Test(expected = ParameterException.class)
    public void cannotGiveBothSourceFileAndUrl() {
        String[] args = new String[]{SOURCE_URL, "http://test.net/file.img", SOURCE_FILE, HELLO_TXT.toString()};
        new JCommander(arguments, args);
        arguments.validate();
    }

    @Test
    public void onlySourceUrlIsValid() {
        String[] args = new String[]{SOURCE_URL, "http://test.net/file.img"};
        new JCommander(arguments, args);
        arguments.validate();
    }

    @Test
    public void onlySourceFileIsValid() {
        String[] args = new String[]{SOURCE_FILE, HELLO_TXT.toString()};
        new JCommander(arguments, args);
        arguments.validate();
    }

    @Test
    public void withHelp() {
        String[] args = new String[]{HELP};
        new JCommander(arguments, args);
        assertThat(arguments.isHelp()).isTrue();
    }

    @Test
    public void withoutHelp() {
        String[] args = new String[]{};
        new JCommander(arguments, args);
        assertThat(arguments.isHelp()).isFalse();
    }
}
